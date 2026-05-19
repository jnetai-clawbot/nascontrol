package com.jnetaol.nascontrol.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jnetaol.nascontrol.NASControlApp
import com.jnetaol.nascontrol.data.model.*
import com.jnetaol.nascontrol.engine.BackupManager
import com.jnetaol.nascontrol.engine.NASConnector
import com.jnetaol.nascontrol.logger.DebugLogger
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as NASControlApp).database
    val connector = NASConnector()
    val backupManager = BackupManager()

    val servers: StateFlow<List<ServerConfig>> = db.serverConfigDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _connectedServer = MutableStateFlow<ServerConfig?>(null)
    val connectedServer: StateFlow<ServerConfig?> = _connectedServer.asStateFlow()

    private val _systemStats = MutableStateFlow(SystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError.asStateFlow()

    val disks: StateFlow<List<DiskInfo>> = connectedServer
        .flatMapLatest { server ->
            server?.let { db.diskInfoDao().getByServer(it.id) }
                ?: flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val containers: StateFlow<List<DockerContainer>> = connectedServer
        .flatMapLatest { server ->
            server?.let { db.dockerContainerDao().getByServer(it.id) }
                ?: flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val backupTasks: StateFlow<List<BackupTask>> = db.backupTaskDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun connectToServer(server: ServerConfig) {
        viewModelScope.launch {
            _isConnecting.value = true
            _connectionError.value = null
            DebugLogger.i("NC-001", "Connecting to ${server.name}")

            val result = connector.connect(server)
            if (result.success) {
                db.serverConfigDao().clearConnections()
                db.serverConfigDao().update(
                    server.copy(
                        lastConnected = System.currentTimeMillis(),
                        isConnected = true,
                        isDefault = true
                    )
                )
                result.systemStats?.let { _systemStats.value = it }
                result.disks.let {
                    db.diskInfoDao().deleteByServer(server.id)
                    db.diskInfoDao().insertAll(it)
                }
                result.containers.let {
                    db.dockerContainerDao().deleteByServer(server.id)
                    db.dockerContainerDao().insertAll(it)
                }
                _connectedServer.value = server.copy(isConnected = true)
                DebugLogger.i("NC-002", "Connected to ${server.name}")
            } else {
                _connectionError.value = result.message
                DebugLogger.e("NC-003", "Failed: ${result.message}")
            }
            _isConnecting.value = false
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            _connectedServer.value?.let { server ->
                db.serverConfigDao().update(server.copy(isConnected = false))
            }
            _connectedServer.value = null
            _systemStats.value = SystemStats()
        }
    }

    fun refreshDocker() { refreshStats() }
    fun startContainer(id: String) { containerAction(id, "start") }
    fun stopContainer(id: String) { containerAction(id, "stop") }
    fun restartContainer(id: String) { containerAction(id, "restart") }

    fun refreshStats() {
        viewModelScope.launch {
            _connectedServer.value?.let { server ->
                try {
                    _systemStats.value = connector.fetchSystemStats(server)
                    val newDisks = connector.fetchDisks(server)
                    db.diskInfoDao().deleteByServer(server.id)
                    db.diskInfoDao().insertAll(newDisks)
                    val newContainers = connector.fetchContainers(server)
                    db.dockerContainerDao().deleteByServer(server.id)
                    db.dockerContainerDao().insertAll(newContainers)
                } catch (e: Exception) {
                    DebugLogger.e("NC-004", "Refresh failed: ${e.message}", e)
                }
            }
        }
    }

    fun addServer(name: String, host: String, port: Int, username: String, password: String, type: String) {
        viewModelScope.launch {
            val config = ServerConfig(
                name = name,
                host = host,
                port = port,
                username = username,
                password = password,
                connectionType = type
            )
            db.serverConfigDao().insert(config)
            DebugLogger.i("NC-005", "Server added: $name")
        }
    }

    fun updateServer(config: ServerConfig) {
        viewModelScope.launch {
            db.serverConfigDao().update(config)
        }
    }

    fun deleteServer(config: ServerConfig) {
        viewModelScope.launch {
            db.diskInfoDao().deleteByServer(config.id)
            db.dockerContainerDao().deleteByServer(config.id)
            db.serverConfigDao().delete(config)
            if (_connectedServer.value?.id == config.id) {
                disconnect()
            }
        }
    }

    fun setDefaultServer(config: ServerConfig) {
        viewModelScope.launch {
            db.serverConfigDao().clearDefaults()
            db.serverConfigDao().update(config.copy(isDefault = true))
        }
    }

    fun containerAction(containerId: String, action: String) {
        viewModelScope.launch {
            _connectedServer.value?.let { server ->
                connector.containerAction(server, containerId, action)
                val newContainers = connector.fetchContainers(server)
                db.dockerContainerDao().deleteByServer(server.id)
                db.dockerContainerDao().insertAll(newContainers)
            }
        }
    }

    private var fileListCache = mutableMapOf<String, MutableStateFlow<List<FileItem>>>()

    fun getFileList(path: String): StateFlow<List<FileItem>> {
        return fileListCache.getOrPut(path) {
            val flow = MutableStateFlow<List<FileItem>>(emptyList())
            viewModelScope.launch {
                _connectedServer.value?.let { server ->
                    flow.value = connector.fetchFileList(server, path)
                }
            }
            flow
        }
    }

    fun navigateToFile(path: String): StateFlow<List<FileItem>> {
        fileListCache.remove(path)
        return getFileList(path)
    }

    fun addBackupTask(name: String, source: String, dest: String, schedule: String, serverId: Long) {
        viewModelScope.launch {
            val task = BackupTask(
                serverId = serverId,
                name = name,
                sourcePath = source,
                destPath = dest,
                schedule = schedule,
                nextRun = backupManager.calculateNextRun(schedule)
            )
            db.backupTaskDao().insert(task)
        }
    }

    fun deleteBackupTask(task: BackupTask) {
        viewModelScope.launch {
            db.backupTaskDao().delete(task)
        }
    }

    fun runBackup(task: BackupTask) {
        viewModelScope.launch {
            db.backupTaskDao().update(task.copy(status = "Running", progress = 0))
            val success = backupManager.simulateBackup(task)
            val newStatus = if (success) "Completed" else "Failed"
            db.backupTaskDao().update(
                task.copy(
                    status = newStatus,
                    lastRun = System.currentTimeMillis(),
                    nextRun = backupManager.calculateNextRun(task.schedule),
                    progress = if (success) 100 else 0
                )
            )
        }
    }

    fun dismissAlert(alertId: String) {
        _alerts.value = _alerts.value.filter { it.id != alertId }
    }

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun showToast(msg: String) { viewModelScope.launch { _toastMessage.emit(msg) } }
}
