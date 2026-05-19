package com.jnetaol.nascontrol.ui.screens.docker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.nascontrol.data.model.DockerContainer
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockerScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val containers by viewModel.containers.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = OnBackground) }
            Text("Docker Containers", color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            GlowButton("Refresh", onClick = { viewModel.refreshDocker() }, icon = Icons.Default.Refresh)
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(containers) { container ->
                ContainerCard(container, onStart = { viewModel.containerAction(container.containerId, "start") }, onStop = { viewModel.containerAction(container.containerId, "stop") }, onRestart = { viewModel.containerAction(container.containerId, "restart") })
            }
        }
    }
}

@Composable
fun ContainerCard(container: DockerContainer, onStart: () -> Unit, onStop: () -> Unit, onRestart: () -> Unit) {
    NeonCard {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(if (container.status.lowercase() == "running") StatusGood else StatusStopped))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(container.name, color = OnBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${container.image} • ${container.status}", color = OnSurface, fontSize = 13.sp)
                if (container.ports.isNotBlank()) Text("Ports: ${container.ports}", color = OnSurface, fontSize = 12.sp)
            }
            Row {
                IconButton(onStart, Modifier.size(36.dp)) { Icon(Icons.Default.PlayArrow, "Start", tint = NeonGreen, modifier = Modifier.size(20.dp)) }
                IconButton(onStop, Modifier.size(36.dp)) { Icon(Icons.Default.Stop, "Stop", tint = NeonRed, modifier = Modifier.size(20.dp)) }
                IconButton(onRestart, Modifier.size(36.dp)) { Icon(Icons.Default.Refresh, "Restart", tint = NeonYellow, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}
