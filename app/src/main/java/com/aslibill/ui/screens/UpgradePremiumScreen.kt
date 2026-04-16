package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AsliColors

@Composable
fun UpgradePremiumScreen(
    contentPadding: PaddingValues,
    vm: UpgradePremiumViewModel,
    onBack: () -> Unit
) {
    val features by vm.features.collectAsState()

    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AsliColors.Bg,
                            AsliColors.Bg,
                            AsliColors.Orange.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            // Header with Premium Icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AsliColors.Orange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Premium",
                        tint = AsliColors.Orange,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Upgrade to Premium",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AsliColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Take your business to the next level",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AsliColors.TextSecondary
                )
            }

            // Features List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(features) { feature ->
                    PremiumFeatureRow(feature)
                }
            }

            // Bottom CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Just ₹999 / Year",
                    style = MaterialTheme.typography.titleLarge,
                    color = AsliColors.Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                
                Text(
                    text = "Less than ₹3 per day",
                    style = MaterialTheme.typography.labelMedium,
                    color = AsliColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                        .height(56.dp)
                )
                
                TextButton(onClick = onBack) {
                    Text("Maybe Later", color = AsliColors.TextSecondary)
                }
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
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = AsliColors.Green,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                color = AsliColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = AsliColors.TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
