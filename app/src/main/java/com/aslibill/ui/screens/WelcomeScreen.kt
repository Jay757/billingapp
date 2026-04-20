package com.aslibill.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.ui.theme.Brand

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Theme-adjusted colors
    val bgGradientStart = if (isDark) primaryColor else Color(0xFFE0F2FE) // Light Blue 50
    val bgGradientEnd = if (isDark) Color(0xFF020617) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF0F172A) // Slate 900
    val buttonBg = if (isDark) Color.White else primaryColor
    val buttonText = if (isDark) primaryColor else Color.White
    
    val gradientColors = listOf(
        bgGradientStart,
        if (isDark) primaryColor.copy(alpha = 0.6f) else bgGradientStart.copy(alpha = 0.5f),
        bgGradientEnd
    )

    com.aslibill.ui.components.ScreenSurface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Decorative Abstract Shapes
            DecorativeBackground(isDark)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Logo Section
                PremiumLogo(isDark)

                Spacer(modifier = Modifier.height(24.dp))

                // App Name
                Text(
                    text = Brand.AppName,
                    color = textColor,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                )

                Spacer(modifier = Modifier.weight(1.2f))

                // Buttons Section
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg,
                        contentColor = buttonText
                    ),
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isDark) 4.dp else 8.dp)
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = buttonText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "I already have an account",
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onGoToLogin() }
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DecorativeBackground(isDark: Boolean) {
    val shapeColor = if (isDark) Color.White else Color.Black
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw some diagonal translucent lines/rects
        rotate(degrees = -35f) {
            // Large diagonal pill shape
            drawRoundRect(
                color = shapeColor.copy(alpha = if (isDark) 0.05f else 0.03f),
                topLeft = Offset(width * 0.1f, -height * 0.2f),
                size = Size(width * 0.4f, height * 0.8f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f)
            )

            // Smaller diagonal pill shape
            drawRoundRect(
                color = shapeColor.copy(alpha = if (isDark) 0.08f else 0.04f),
                topLeft = Offset(width * 0.6f, height * 0.1f),
                size = Size(width * 0.2f, height * 0.6f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(80f, 80f)
            )
            
            // Thin accent lines
             drawRect(
                color = shapeColor.copy(alpha = if (isDark) 0.1f else 0.05f),
                topLeft = Offset(width * 0.3f, -height * 0.5f),
                size = Size(2.dp.toPx(), height * 1.5f)
            )
            
            drawRect(
                color = shapeColor.copy(alpha = if (isDark) 0.1f else 0.05f),
                topLeft = Offset(width * 0.75f, -height * 0.3f),
                size = Size(3.dp.toPx(), height * 1.2f)
            )
        }
    }
}

@Composable
fun PremiumLogo(isDark: Boolean) {
    val boxBg = if (isDark) {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f)))
    } else {
        Brush.linearGradient(listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.02f)))
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(boxBg)
            .padding(24.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokeWidth = 14.dp.toPx()
            val silverGradient = if (isDark) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFF94A3B8)),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569)),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            }

            // Draw a stylized 'N' or Logo shape based on image reference
            // Image shows three horizontal bars with circles at start
            val spacing = h / 4
            
            // Top bar
            drawRoundRect(
                brush = silverGradient,
                topLeft = Offset(w * 0.2f, spacing * 0.5f),
                size = Size(w * 0.7f, strokeWidth),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth/2, strokeWidth/2)
            )
            
            // Middle bar (with circle)
            drawCircle(
                brush = silverGradient,
                radius = strokeWidth * 1.2f,
                center = Offset(w * 0.15f, spacing * 2f)
            )
            drawRoundRect(
                brush = silverGradient,
                topLeft = Offset(w * 0.3f, spacing * 2f - strokeWidth/2),
                size = Size(w * 0.6f, strokeWidth),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth/2, strokeWidth/2)
            )
            
            // Bottom bar
            drawRoundRect(
                brush = silverGradient,
                topLeft = Offset(w * 0.2f, spacing * 3.5f - strokeWidth),
                size = Size(w * 0.7f, strokeWidth),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth/2, strokeWidth/2)
            )
            
            // Connecting vertical line at start
            drawRoundRect(
                brush = silverGradient,
                topLeft = Offset(w * 0.2f, spacing * 0.5f),
                size = Size(strokeWidth, spacing * 3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth/2, strokeWidth/2)
            )
        }
    }
}
