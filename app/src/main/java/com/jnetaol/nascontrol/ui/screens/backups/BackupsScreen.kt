package com.jnetaol.nascontrol.ui.screens.backups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.nascontrol.data.model.BackupTask
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupsScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val tasks by viewModel.backupTasks.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = OnBackground) }
            Text("Backups", color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks) { task -> BackupCard(task, onRun = { viewModel.runBackup(task) }, onDelete = { viewModel.deleteBackupTask(task) }) }
        }
    }
}

@Composable
fun BackupCard(task: BackupTask, onRun: () -> Unit, onDelete: () -> Unit) {
    NeonCard {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(task.name, color = OnBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                StatusBadge(task.status)
            }
            Spacer(Modifier.height(8.dp))
            Text("Source: ${task.sourcePath}", color = OnBackground, fontSize = 12.sp)
            Text("Dest: ${task.destPath}", color = OnBackground, fontSize = 12.sp)
            Text("Schedule: ${task.schedule}", color = OnBackground, fontSize = 12.sp)
            if (task.progress > 0) LinearProgressIndicator(progress = { task.progress / 100f }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.End) {
                GlowButton("Run", Icons.Default.PlayArrow, glowColor = NeonGreen, onClick = onRun)
                Spacer(Modifier.width(8.dp))
                GlowButton("Delete", Icons.Default.Delete, glowColor = NeonRed, onClick = onDelete)
            }
        }
    }
}
