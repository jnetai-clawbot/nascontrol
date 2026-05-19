package com.jnetaol.nascontrol.ui.screens.files

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
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    var currentPath by remember { mutableStateOf("/") }
    val fileList by viewModel.getFileList(currentPath).collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = OnBackground) }
            Text("Files", color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Folder, null, tint = NeonTeal)
            Spacer(Modifier.width(8.dp))
            Text(currentPath, color = OnSurface, fontSize = 14.sp)
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (currentPath != "/") {
                item {
                    Surface(Modifier.fillMaxWidth(), onClick = { currentPath = currentPath.substringBeforeLast("/", "/") }, shape = RoundedCornerShape(8.dp), color = SurfaceVariant) {
                        Row(Modifier.padding(12.dp)) { Icon(Icons.Default.ArrowUpward, null, tint = NeonCyan); Spacer(Modifier.width(12.dp)); Text("..", color = OnSurface) }
                    }
                }
            }
            items(fileList) { file ->
                Surface(Modifier.fillMaxWidth(), onClick = { if (file.isDirectory) currentPath = file.path }, shape = RoundedCornerShape(8.dp), color = Surface) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile, null, tint = if (file.isDirectory) NeonTeal else OnSurface)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(file.name, color = OnSurface, fontSize = 14.sp); if (!file.isDirectory) Text(formatBytes(file.size), color = OnBackground, fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}
