package com.aslibill.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
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
    val plans by vm.plans.collectAsState()
    val selectedPlan by vm.selectedPlan.collectAsState()
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
                        .padding(top = AppSpacing.xl, bottom = AppSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(if (LocalConfiguration.current.screenWidthDp > 600) 100.dp else 80.dp)
                            .clip(CircleShape),
                        color = AsliColors.AlertOrange.copy(alpha = 0.1f)
                    ) {

                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Premium",
                                tint = AsliColors.AlertOrange,
                                modifier = Modifier.size(if (LocalConfiguration.current.screenWidthDp > 600) 56.dp else 48.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    
                    Text(
                        text = "UPGRADE TO PREMIUM",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = "Unlock all features for your business",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = AppSpacing.xs)
                    )
                }

                // Subscription Plans
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    plans.forEach { plan ->
                        SubscriptionPlanCard(
                            plan = plan,
                            isSelected = selectedPlan.id == plan.id,
                            onClick = { vm.selectPlan(plan) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = "WHAT'S INCLUDED?",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = AppSpacing.xl, vertical = AppSpacing.sm)
                )

                // Features List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                    contentPadding = PaddingValues(bottom = AppSpacing.xl)
                ) {
                    items(features) { feature ->
                        PremiumFeatureRow(feature)
                    }
                }

                // Bottom CTA
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {

                    Column(
                        modifier = Modifier.padding(AppSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "15-DAY FREE TRIAL INCLUDED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AsliColors.SuccessGreen,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = AppSpacing.md)
                        )

                        OrangeButton(
                            text = "START 15-DAY FREE TRIAL",
                            onClick = {
                                vm.performUpgrade {
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        
                        Text(
                            text = "Then ${selectedPlan.priceLabel}. Cancel anytime.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = AppSpacing.sm)
                        )
                        
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                "NOT NOW, THANKS",
                                color = AsliColors.TextSecondary,
                                style = AppTypography.labelCaps.copy(fontSize = 10.sp)
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
fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                ) else Modifier
            )
            .padding(1.dp) // Space for border
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            color = backgroundColor,
            border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = AppSpacing.md, horizontal = AppSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (plan.tag != null) {
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else AsliColors.AlertOrange,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = plan.tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp)) // Maintain alignment
                }

                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Text(
                    text = plan.priceLabel.split("/")[0],
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(16.dp)
            )
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
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black, fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }

    }
}
