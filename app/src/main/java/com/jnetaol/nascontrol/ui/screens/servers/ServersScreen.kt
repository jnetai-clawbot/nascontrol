package com.jnetaol.nascontrol.ui.screens.servers

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
import com.jnetaol.nascontrol.data.model.ServerConfig
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val servers by viewModel.servers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("22") }
    var username by remember { mutableStateOf("root") }
    var password by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = OnBackground) }
            Text("Servers", color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            GlowButton("Add", onClick = { showAddDialog = true }, icon = Icons.Default.Add)
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(servers) { server -> ServerCard(server, onConnect = { viewModel.connectToServer(server) }, onDelete = { viewModel.deleteServer(server) }) }
        }
    }

    if (showAddDialog) AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Add Server", color = OnBackground) }, text = {
        Column { OutlinedTextField(name, { name = it }, label = { Text("Name") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground)); Spacer(Modifier.height(8.dp))
            OutlinedTextField(host, { host = it }, label = { Text("Host/IP") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground)); Spacer(Modifier.height(8.dp))
            OutlinedTextField(port, { port = it }, label = { Text("Port") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground)); Spacer(Modifier.height(8.dp))
            OutlinedTextField(username, { username = it }, label = { Text("Username") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground)); Spacer(Modifier.height(8.dp))
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground))
        }
    }, confirmButton = { TextButton({ viewModel.addServer(name, host, port.toIntOrNull() ?: 22, username, password, "SSH"); showAddDialog = false }) { Text("Add", color = NeonTeal) } }, dismissButton = { TextButton({ showAddDialog = false }) { Text("Cancel", color = OnSurface) } }, containerColor = Surface)
}

@Composable
fun ServerCard(server: ServerConfig, onConnect: () -> Unit, onDelete: () -> Unit) {
    NeonCard {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(server.name, color = OnBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("${server.host}:${server.port}", color = OnSurface, fontSize = 13.sp); if (server.isConnected) Text("Connected", color = NeonGreen, fontSize = 12.sp) }
            Column { GlowButton("Connect", onClick = onConnect, icon = Icons.Default.PowerSettingsNew); Spacer(Modifier.height(4.dp)); GlowButton("Delete", onClick = onDelete, icon = Icons.Default.Delete) }
        }
    }
}
