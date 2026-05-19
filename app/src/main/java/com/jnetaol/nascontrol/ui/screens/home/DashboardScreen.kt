package com.jnetaol.nascontrol.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jnetaol.nascontrol.data.model.Alert
import com.jnetaol.nascontrol.data.model.SystemStats
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToServers: () -> Unit
) {
    val stats by viewModel.systemStats.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val connected by viewModel.connectedServer.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()

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
                    text = "Dashboard",
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

        if (connected != null) {
            item {
                NeonCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Cloud, contentDescription = null,
                            tint = StatusRunning, modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Connected to ${connected!!.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusRunning
                            )
                            Text(
                                text = "${connected!!.host}:${connected!!.port} via ${connected!!.connectionType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnBackground.copy(alpha = 0.6f)
                            )
                        }
                        TextButton(onClick = { viewModel.disconnect() }) {
                            Text("Disconnect", color = StatusCritical)
                        }
                    }
                }
            }

            item { SectionHeader("System Resources") }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "CPU",
                        value = "${String.format("%.1f", stats.cpuPercent)}%",
                        icon = Icons.Default.Memory,
                        subtitle = "${stats.cpuCores} cores | ${String.format("%.1f", stats.cpuTemp)}°C",
                        progress = stats.cpuPercent / 100f,
                        modifier = Modifier.weight(1f)
                    )
                    StatsCard(
                        title = "RAM",
                        value = "${String.format("%.1f", stats.ramPercent)}%",
                        icon = Icons.Default.Storage,
                        subtitle = "${String.format("%.1f", stats.ramUsedGb)} / ${String.format("%.1f", stats.ramTotalGb)} GB",
                        progress = stats.ramPercent / 100f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Network Down",
                        value = stats.networkRxSpeed,
                        icon = Icons.Default.Download,
                        modifier = Modifier.weight(1f),
                        valueColor = NeonBlue
                    )
                    StatsCard(
                        title = "Network Up",
                        value = stats.networkTxSpeed,
                        icon = Icons.Default.Upload,
                        modifier = Modifier.weight(1f),
                        valueColor = NeonGreen
                    )
                }
            }

            item {
                NeonCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Load Average",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnBackground.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = stats.loadAverage,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NeonTeal
                                )
                            }
                            Column {
                                Text(
                                    text = "Uptime",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnBackground.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = stats.uptime,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NeonTeal
                                )
                            }
                            Column {
                                Text(
                                    text = "OS",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnBackground.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = stats.osVersion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            if (alerts.isNotEmpty()) {
                item { SectionHeader("Alerts") }
                items(alerts) { alert ->
                    AlertBanner(
                        title = alert.title,
                        message = alert.message,
                        severity = alert.severity,
                        onDismiss = { viewModel.dismissAlert(alert.id) }
                    )
                }
            }
        } else {
            item {
                EmptyState(
                    icon = Icons.Default.CloudOff,
                    title = "Not Connected",
                    subtitle = "Add and connect to a NAS server to start monitoring"
                )
            }

            item {
                GlowButton(
                    text = "Manage Servers",
                    onClick = onNavigateToServers,
                    icon = Icons.Default.Dns,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (isConnecting) {
            item {
                NeonCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = NeonCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Connecting...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
