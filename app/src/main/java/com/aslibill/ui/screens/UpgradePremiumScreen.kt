package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors

@Composable
fun UpgradePremiumScreen(
    contentPadding: PaddingValues,
    vm: UpgradePremiumViewModel,
    onBack: () -> Unit
) {
    val features by vm.features.collectAsState()

    val isLoading by vm.isLoading.collectAsState()
    
    ScreenSurface {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    )

            ) {
                // Header with Premium Icon
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(if (LocalConfiguration.current.screenWidthDp > 600) 120.dp else 100.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {

                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Premium",
                                tint = AsliColors.AlertOrange,
                                modifier = Modifier.size(if (LocalConfiguration.current.screenWidthDp > 600) 64.dp else 56.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppSpacing.lg))
                    
                    Text(
                        text = "UPGRADE TO PREMIUM",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = "Take your business to the next level",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = AppSpacing.xs)
                    )

                }

                // Features List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    items(features) { feature ->
                        PremiumFeatureRow(feature)
                    }
                }

                // Bottom CTA
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(AppSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹999",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " / year",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            text = "Less than ₹3 per day",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AsliColors.SuccessGreen,
                            modifier = Modifier.padding(top = AppSpacing.xs, bottom = AppSpacing.xl)
                        )


                        OrangeButton(
                            text = "UPGRADE NOW",
                            onClick = {
                                vm.performUpgrade {
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                "MAYBE LATER",
                                color = AsliColors.TextSecondary,
                                style = AppTypography.labelCaps
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                com.aslibill.ui.components.AsliLoader()
            }
        }
    }
}

@Composable
fun PremiumFeatureRow(feature: PremiumFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            color = AsliColors.SuccessGreen.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = AsliColors.SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = feature.title.uppercase(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

    }
}
