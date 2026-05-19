package com.jnetaol.nascontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jnetaol.nascontrol.ui.components.BottomNavBar
import com.jnetaol.nascontrol.ui.screens.AppNavHost
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.NASControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASControlTheme {
                val navController = rememberNavController()
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<AppViewModel>()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
