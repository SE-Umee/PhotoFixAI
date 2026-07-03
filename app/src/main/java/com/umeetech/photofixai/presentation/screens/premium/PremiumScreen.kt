package com.umeetech.photofixai.presentation.screens.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.PremiumPlan
import com.umeetech.photofixai.presentation.components.PremiumFeatureItem
import com.umeetech.photofixai.presentation.components.PrimaryButtonFullWidth
import com.umeetech.photofixai.presentation.theme.BrandPrimary
import com.umeetech.photofixai.presentation.theme.BrandPrimaryDark
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun PremiumScreen(onClose: () -> Unit) {
    val viewModel: PremiumViewModel = rememberScopedViewModel { PremiumViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(BrandPrimary, BrandPrimaryDark)))
                .padding(Dimens.SpaceXXL)
        ) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.WorkspacePremium, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(Dimens.SpaceM))
                Text(
                    if (state.isPremium) "You're Premium" else "Go Premium",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Unlock the full power of PhotoFix AI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(Modifier.padding(Dimens.ScreenPadding)) {
            state.benefits.forEach {
                PremiumFeatureItem(text = it)
                Spacer(Modifier.height(Dimens.SpaceM))
            }

            Spacer(Modifier.height(Dimens.SpaceM))
            Text("Choose a plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(Dimens.SpaceM))
            state.plans.forEach { plan ->
                PlanCard(
                    plan = plan,
                    selected = plan.id == state.selectedPlanId,
                    onSelect = { viewModel.selectPlan(plan.id) }
                )
                Spacer(Modifier.height(Dimens.SpaceM))
            }

            Spacer(Modifier.height(Dimens.SpaceM))
            PrimaryButtonFullWidth(
                text = if (state.isPremium) "Manage subscription" else "Continue",
                onClick = viewModel::startPurchase
            )
            Spacer(Modifier.height(Dimens.SpaceS))
            Text(
                "Billing is not enabled in this build. Restore & manage options will appear once Google Play Billing is integrated.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }

    if (state.showComingSoon) {
        AlertDialog(
            onDismissRequest = viewModel::dismissComingSoon,
            title = { Text("Coming soon") },
            text = { Text("In-app purchases will be available in a future update. The upgrade flow is ready for Google Play Billing integration.") },
            confirmButton = { TextButton(onClick = viewModel::dismissComingSoon) { Text("OK") } }
        )
    }
}

@Composable
private fun PlanCard(plan: PremiumPlan, selected: Boolean, onSelect: () -> Unit) {
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(Dimens.RadiusM),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(plan.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (plan.badge != null) {
                        Spacer(Modifier.height(Dimens.SpaceS))
                        Text(
                            "  " + plan.badge,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(plan.period, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(plan.price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
