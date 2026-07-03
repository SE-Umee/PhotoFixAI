package com.umeetech.photofixai.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.BuildConfig
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.ExportQuality
import com.umeetech.photofixai.domain.model.ThemeMode
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.FormatSelector
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun SettingsScreen(onBack: () -> Unit, onOpenPremium: () -> Unit) {
    val viewModel: SettingsViewModel = rememberScopedViewModel { SettingsViewModel(it) }
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = "Settings", onBack = onBack)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            SectionHeader(title = "Appearance")
            Spacer(Modifier.height(Dimens.SpaceM))
            ChoiceRow(
                label = "Theme",
                options = ThemeMode.entries.map { it.label },
                selectedIndex = ThemeMode.entries.indexOf(settings.themeMode),
                onSelect = { viewModel.setTheme(ThemeMode.entries[it]) }
            )

            Spacer(Modifier.height(Dimens.SpaceL))
            SectionHeader(title = "Export")
            Spacer(Modifier.height(Dimens.SpaceM))
            ChoiceRow(
                label = "Export quality",
                options = ExportQuality.entries.map { it.label },
                selectedIndex = ExportQuality.entries.indexOf(settings.exportQuality),
                onSelect = { viewModel.setExportQuality(ExportQuality.entries[it]) }
            )
            Spacer(Modifier.height(Dimens.SpaceM))
            Text("Default format", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(Dimens.SpaceS))
            FormatSelector(
                selected = settings.defaultFormat,
                onSelect = viewModel::setDefaultFormat,
                modifier = Modifier.fillMaxWidth(),
                options = ExportFormat.entries
            )

            Spacer(Modifier.height(Dimens.SpaceL))
            SectionHeader(title = "About & legal")
            Spacer(Modifier.height(Dimens.SpaceS))
            SettingsRow(Icons.Filled.PrivacyTip, "Privacy Policy") { context.openUrl("https://photofixai.app/privacy") }
            SettingsRow(Icons.Filled.Description, "Terms & Conditions") { context.openUrl("https://photofixai.app/terms") }
            SettingsRow(Icons.Filled.StarRate, "Rate App") {
                context.openUrl("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            SettingsRow(Icons.Filled.Share, "Share App") { context.shareApp() }
            SettingsRow(Icons.AutoMirrored.Filled.ContactSupport, "Contact Support") { context.emailSupport() }

            Spacer(Modifier.height(Dimens.SpaceL))
            SectionHeader(title = "Storage")
            Spacer(Modifier.height(Dimens.SpaceS))
            SettingsRow(Icons.Filled.CleaningServices, "Clear Cache") { viewModel.clearCache(context) {} }
            SettingsRow(Icons.Filled.DeleteSweep, "Clear History") { viewModel.clearHistory() }

            Spacer(Modifier.height(Dimens.SpaceXL))
            Text(
                "PhotoFix AI · v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }
}

@Composable
private fun ChoiceRow(label: String, options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(Dimens.SpaceS))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)) {
            options.forEachIndexed { index, option ->
                androidx.compose.material3.FilterChip(
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = Dimens.SpaceM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceL)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.height(16.dp)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

private fun android.content.Context.openUrl(url: String) {
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}

private fun android.content.Context.shareApp() {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out PhotoFix AI: https://play.google.com/store/apps/details?id=$packageName"
        )
    }
    runCatching { startActivity(Intent.createChooser(intent, "Share PhotoFix AI")) }
}

private fun android.content.Context.emailSupport() {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:support@photofixai.app")
        putExtra(Intent.EXTRA_SUBJECT, "PhotoFix AI Support")
    }
    runCatching { startActivity(intent) }
}
