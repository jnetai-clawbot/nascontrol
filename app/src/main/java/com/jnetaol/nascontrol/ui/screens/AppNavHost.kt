package com.jnetaol.nascontrol.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jnetaol.nascontrol.ui.screens.backups.BackupsScreen
import com.jnetaol.nascontrol.ui.screens.disks.DisksScreen
import com.jnetaol.nascontrol.ui.screens.docker.DockerScreen
import com.jnetaol.nascontrol.ui.screens.files.FilesScreen
import com.jnetaol.nascontrol.ui.screens.home.DashboardScreen
import com.jnetaol.nascontrol.ui.screens.servers.ServersScreen
import com.jnetaol.nascontrol.ui.screens.settings.SettingsScreen
import com.jnetaol.nascontrol.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavHost(navController: NavHostController, viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    Box(modifier) {
        NavHost(navController, startDestination = "dashboard") {
            composable("dashboard") { DashboardScreen(viewModel, onNavigateToServers = { navController.navigate("servers") }) }
            composable("disks") { DisksScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
            composable("docker") { DockerScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
            composable("files") { FilesScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
            composable("backups") { BackupsScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
            composable("servers") { ServersScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
            composable("settings") { SettingsScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
