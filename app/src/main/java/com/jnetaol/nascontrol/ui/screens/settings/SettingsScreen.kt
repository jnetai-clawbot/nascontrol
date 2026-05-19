package com.jnetaol.nascontrol.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.nascontrol.ui.components.*
import com.jnetaol.nascontrol.ui.screens.AppViewModel
import com.jnetaol.nascontrol.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onNavigateBack) { Icon(Icons.Default.ArrowBack, null, tint = OnBackground) }
            Text("Settings", color = OnBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(48.dp))
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            SectionHeader("About")
            NeonCard {
                SettingsRow(Icons.Default.Info, "NASControl v1.0.0", "NAS Server Manager") {}
                Divider(color = DividerDark)
                SettingsRow(Icons.Default.Language, "Made By jnetaol.com", "Visit our website") {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://jnetaol.com"))) } catch (_: Exception) {}
                }
                Divider(color = DividerDark)
                SettingsRow(Icons.Default.SystemUpdateAlt, "Check For Updates", "See latest on GitHub") {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jnetaol/nascontrol/releases"))) } catch (_: Exception) {}
                }
                Divider(color = DividerDark)
                SettingsRow(Icons.Default.Share, "Share App", "Share latest release") {
                    val intent = Intent(Intent.ACTION_SEND).apply { putExtra(Intent.EXTRA_TEXT, "Check out NASControl: https://github.com/jnetaol/nascontrol/releases"); type = "text/plain" }
                    context.startActivity(Intent.createChooser(intent, "Share"))
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = NeonTeal, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) { Text(title, color = OnBackground, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, color = OnSurface, fontSize = 12.sp) }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurface, modifier = Modifier.size(20.dp))
    }
}
