package com.jnetaol.nascontrol.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.nascontrol.data.model.DockerContainer
import com.jnetaol.nascontrol.data.model.DiskInfo
import com.jnetaol.nascontrol.data.model.SystemStats
import com.jnetaol.nascontrol.ui.theme.*
import java.text.DecimalFormat

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: List<Color> = listOf(NeonTeal, NeonCyan)
) {
    val brush = Brush.horizontalGradient(colors)
    Button(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .border(1.dp, NeonTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnPrimary,
            disabledContainerColor = SurfaceVariant,
            disabledContentColor = OnBackground.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = NeonTeal)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                brush = brush,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .border(0.5.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = NeonCyan,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = OnBackground.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = OnBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val color = when (status.lowercase()) {
        "running", "good", "active", "completed" -> StatusRunning
        "stopped", "idle" -> StatusStopped
        "warning" -> StatusWarning
        "critical", "failed" -> StatusCritical
        "paused" -> StatusPaused
        else -> StatusStopped
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    valueColor: Color = NeonCyan,
    progress: Float = -1f
) {
    NeonCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = valueColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnBackground.copy(alpha = 0.5f)
            )
        }
        if (progress >= 0f) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (progress > 0.8f) StatusCritical else if (progress > 0.6f) StatusWarning else StatusRunning,
                trackColor = SurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressGauge(
    title: String,
    progress: Float,
    maxValue: String,
    usedValue: String,
    modifier: Modifier = Modifier,
    healthStatus: String = "Good"
) {
    val gaugeColor = when {
        progress > 0.9f -> StatusCritical
        progress > 0.75f -> StatusWarning
        else -> StatusRunning
    }
    val healthColor = when (healthStatus.lowercase()) {
        "good" -> StatusGood
        "warning" -> StatusWarning
        "critical" -> StatusCritical
        else -> StatusGood
    }
    NeonCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            StatusBadge(healthStatus)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(gaugeColor, gaugeColor.copy(alpha = 0.6f))
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$usedValue used",
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "$maxValue total",
                style = MaterialTheme.typography.bodySmall,
                color = OnBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ContainerCard(
    container: DockerContainer,
    onStart: () -> Unit = {},
    onStop: () -> Unit = {},
    onRestart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isRunning = container.status == "running"
    NeonCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Adb, contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isRunning) StatusRunning else StatusStopped
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = container.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = container.image,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnBackground.copy(alpha = 0.5f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
            StatusBadge(container.status)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isRunning) {
                Text(
                    text = "CPU: ${String.format("%.1f", container.cpuPercent)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "MEM: ${container.memUsage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "Up: ${container.uptime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "Container stopped",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.4f)
                )
            }
        }
        if (container.ports.isNotEmpty()) {
            Text(
                text = "Ports: ${container.ports}",
                style = MaterialTheme.typography.labelSmall,
                color = NeonTeal.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            if (isRunning) {
                GlowButton(
                    text = "Stop",
                    onClick = onStop,
                    colors = listOf(StatusCritical, StatusWarning),
                    modifier = Modifier.height(32.dp)
                )
                GlowButton(
                    text = "Restart",
                    onClick = onRestart,
                    colors = listOf(StatusWarning, NeonOrange),
                    modifier = Modifier.height(32.dp)
                )
            } else {
                GlowButton(
                    text = "Start",
                    onClick = onStart,
                    colors = listOf(StatusGood, StatusRunning),
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: androidx.navigation.NavController) {
    val items = listOf(
        Triple("dashboard", Icons.Default.Dashboard, "Dashboard"),
        Triple("disks", Icons.Default.Storage, "Disks"),
        Triple("docker", Icons.Default.Widgets, "Docker"),
        Triple("files", Icons.Default.Folder, "Files"),
        Triple("backups", Icons.Default.Backup, "Backups")
    )
    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = null).value?.destination?.route

    NavigationBar(
        containerColor = Surface,
        contentColor = OnSurface,
        tonalElevation = 0.dp
    ) {
        items.forEach { (route, icon, label) ->
            val selected = currentRoute == route
            NavigationBarItem(
                icon = {
                    Icon(
                        icon, contentDescription = label,
                        modifier = Modifier.size(22.dp),
                        tint = if (selected) NeonTeal else OnBackground.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) NeonTeal else OnBackground.copy(alpha = 0.5f)
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NeonTeal.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun AlertBanner(
    title: String,
    message: String,
    severity: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val severityColor = when (severity.lowercase()) {
        "critical" -> StatusCritical
        "warning" -> StatusWarning
        else -> NeonCyan
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = severityColor.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning, contentDescription = null,
                tint = severityColor, modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close, contentDescription = "Dismiss",
                    tint = OnBackground.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (63 - java.lang.Long.numberOfLeadingZeros(bytes)) / 10
    val value = bytes.toDouble() / (1L shl (digitGroups * 10))
    return DecimalFormat("#,##0.#").format(value) + " " + units[digitGroups.coerceAtMost(4)]
}

fun formatTimestamp(millis: Long): String {
    if (millis <= 0) return "Never"
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)
    return sdf.format(java.util.Date(millis))
}
