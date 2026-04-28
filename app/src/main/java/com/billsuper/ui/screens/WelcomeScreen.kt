package com.billsuper.ui.screens

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
import com.billsuper.ui.theme.Brand

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Theme-adjusted colors
    val bgGradientStart = if (isDark) primaryColor else MaterialTheme.colorScheme.surfaceVariant
    val bgGradientEnd = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val buttonBg = if (isDark) Color.White else primaryColor
    val buttonText = if (isDark) primaryColor else Color.White
    
    val gradientColors = listOf(
        bgGradientStart,
        if (isDark) primaryColor.copy(alpha = 0.6f) else bgGradientStart.copy(alpha = 0.5f),
        bgGradientEnd
    )

    com.billsuper.ui.components.ScreenSurface {
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
    val shapeColor = MaterialTheme.colorScheme.onBackground
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
    
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    
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
                    colors = listOf(
                        onSurface.copy(alpha = 0.1f),
                        onSurface.copy(alpha = 0.2f),
                        onSurface.copy(alpha = 0.4f)
                    ),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.8f),
                        primary,
                        primary.copy(alpha = 0.9f)
                    ),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
            }

            // Draw a stylized 'B' for BillSuper
            val path = androidx.compose.ui.graphics.Path().apply {
                val startX = w * 0.25f
                val endX = w * 0.75f
                val midY = h * 0.5f
                val topY = h * 0.15f
                val bottomY = h * 0.85f

                // Vertical line (back of the B)
                moveTo(startX, topY)
                lineTo(startX, bottomY)

                // Top loop
                moveTo(startX, topY)
                cubicTo(
                    x1 = endX, y1 = topY,
                    x2 = endX, y2 = midY,
                    x3 = startX, y3 = midY
                )

                // Bottom loop
                moveTo(startX, midY)
                cubicTo(
                    x1 = endX + w * 0.1f, y1 = midY,
                    x2 = endX + w * 0.1f, y2 = bottomY,
                    x3 = startX, y3 = bottomY
                )
            }

            drawPath(
                path = path,
                brush = silverGradient,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }
    }
}

