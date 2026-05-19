package com.jnetaol.nascontrol.ui.screens.disks

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@Composable
fun DisksScreen(viewModel: AppViewModel) {
    val disks by viewModel.disks.collectAsState()
    val connected by viewModel.connectedServer.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Disk Health",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NeonCyan
                )
                IconButton(onClick = { viewModel.refreshStats() }) {
                    Icon(
                        Icons.Default.Refresh, contentDescription = "Refresh",
                        tint = NeonTeal
                    )
                }
            }
        }

        if (connected == null) {
            item {
                EmptyState(
                    icon = Icons.Default.Storage,
                    title = "Not Connected",
                    subtitle = "Connect to a NAS to view disk health"
                )
            }
        } else if (disks.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Storage,
                    title = "No Disks Found",
                    subtitle = "No disk information available"
                )
            }
        } else {
            items(disks) { disk ->
                DiskDetailCard(disk)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun DiskDetailCard(disk: com.jnetaol.nascontrol.data.model.DiskInfo) {
    val usageRatio = if (disk.totalBytes > 0) {
        disk.usedBytes.toFloat() / disk.totalBytes.toFloat()
    } else 0f

    NeonCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Storage, contentDescription = null,
                    tint = if (usageRatio > 0.85f) StatusCritical else NeonTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = disk.diskName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "${disk.filesystem} | ${disk.devicePath}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnBackground.copy(alpha = 0.5f)
                    )
                }
            }
            StatusBadge(disk.healthStatus)
        }

        Spacer(modifier = Modifier.height(12.dp))

        ProgressGauge(
            title = "",
            progress = usageRatio,
            maxValue = formatBytes(disk.totalBytes),
            usedValue = formatBytes(disk.usedBytes),
            healthStatus = disk.healthStatus
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Thermostat, contentDescription = null,
                    tint = if (disk.temperature > 45) StatusWarning else NeonTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${disk.temperature}°C",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "Free: ${formatBytes(disk.freeBytes)}",
                style = MaterialTheme.typography.bodySmall,
                color = StatusRunning
            )
            Text(
                text = "Mount: ${disk.mountPoint}",
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground.copy(alpha = 0.5f)
            )
        }
    }
}
