package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AsliColors

@Composable
fun SubscriptionScreen(
    contentPadding: PaddingValues,
    vm: SubscriptionViewModel
) {
    val currentPlan by vm.currentPlan.collectAsState()
    val features by vm.premiumFeatures.collectAsState()

    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeader("Subscription")

            // Current Plan Card
            DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (currentPlan == PlanType.PREMIUM) AsliColors.Orange else AsliColors.TextSecondary,
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text(
                            "Current Plan",
                            color = AsliColors.TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            currentPlan.name,
                            color = AsliColors.TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                "Premium Features",
                color = AsliColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(features) { feature ->
                    FeatureItem(feature)
                }
            }

            if (currentPlan == PlanType.FREE) {
                OrangeButton(
                    "UPGRADE TO PREMIUM",
                    onClick = { vm.upgrade() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(feature: PlanFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = AsliColors.Orange,
            modifier = Modifier.size(20.dp)
        )
        Text(
            feature.feature,
            color = AsliColors.TextPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
