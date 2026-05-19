package com.jnetaol.nascontrol.data.db

import androidx.room.*
import com.jnetaol.nascontrol.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerConfigDao {
    @Query("SELECT * FROM server_configs ORDER BY lastConnected DESC")
    fun getAll(): Flow<List<ServerConfig>>

    @Query("SELECT * FROM server_configs WHERE id = :id")
    suspend fun getById(id: Long): ServerConfig?

    @Query("SELECT * FROM server_configs WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): ServerConfig?

    @Query("SELECT * FROM server_configs WHERE isConnected = 1 LIMIT 1")
    suspend fun getConnected(): ServerConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: ServerConfig): Long

    @Update
    suspend fun update(config: ServerConfig)

    @Delete
    suspend fun delete(config: ServerConfig)

    @Query("UPDATE server_configs SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE server_configs SET isConnected = 0")
    suspend fun clearConnections()
}

@Dao
interface DockerContainerDao {
    @Query("SELECT * FROM docker_containers WHERE serverId = :serverId ORDER BY name")
    fun getByServer(serverId: Long): Flow<List<DockerContainer>>

    @Query("SELECT * FROM docker_containers WHERE serverId = :serverId AND status = 'running'")
    fun getRunningByServer(serverId: Long): Flow<List<DockerContainer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<DockerContainer>)

    @Query("DELETE FROM docker_containers WHERE serverId = :serverId")
    suspend fun deleteByServer(serverId: Long)
}

@Dao
interface DiskInfoDao {
    @Query("SELECT * FROM disk_infos WHERE serverId = :serverId ORDER BY mountPoint")
    fun getByServer(serverId: Long): Flow<List<DiskInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disks: List<DiskInfo>)

    @Query("DELETE FROM disk_infos WHERE serverId = :serverId")
    suspend fun deleteByServer(serverId: Long)
}

@Dao
interface BackupTaskDao {
    @Query("SELECT * FROM backup_tasks WHERE serverId = :serverId ORDER BY name")
    fun getByServer(serverId: Long): Flow<List<BackupTask>>

    @Query("SELECT * FROM backup_tasks ORDER BY name")
    fun getAll(): Flow<List<BackupTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: BackupTask): Long

    @Update
    suspend fun update(task: BackupTask)

    @Delete
    suspend fun delete(task: BackupTask)

    @Query("DELETE FROM backup_tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
