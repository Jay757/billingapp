package com.aslibill.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.Brand
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.ShoppingCart

@Composable
fun ScreenSurface(content: @Composable () -> Unit) {
  Surface(color = MaterialTheme.colorScheme.background, content = content)
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().padding(top = AppSpacing.lg, bottom = AppSpacing.sm)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      ),
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(AppSpacing.sm))
    Box(
      Modifier
        .fillMaxWidth()
        .height(2.dp)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    )
  }
}

@Composable
fun DarkCard(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    content: @Composable () -> Unit
) {
  Card(
    modifier = modifier.shadow(
      elevation = 4.dp,
      shape = RoundedCornerShape(20.dp),
      spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
    ),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) { content() }
}

@Composable
fun IconTile(
  label: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconSize: Dp = 28.dp
) {
  Column(
    modifier = modifier
      .padding(AppSpacing.xs)
      .clip(RoundedCornerShape(16.dp))
      .border(
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        RoundedCornerShape(16.dp)
      )
      .background(MaterialTheme.colorScheme.surface)
      .clickable(onClick = onClick)
      .padding(vertical = AppSpacing.lg, horizontal = AppSpacing.sm),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon, 
        contentDescription = null, 
        tint = MaterialTheme.colorScheme.primary, 
        modifier = Modifier.size(iconSize)
      )
    }
    Spacer(modifier = Modifier.height(AppSpacing.md))
    Text(
      text = label,
      color = MaterialTheme.colorScheme.onSurface,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold, 
        fontSize = 12.sp
      ),
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center,
      lineHeight = 16.sp
    )
  }
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(20.dp),
            spotColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(
                    text = label, 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), 
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value, 
                    color = MaterialTheme.colorScheme.onSurface, 
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}

@Composable
fun OrangeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
  Button(
    onClick = onClick,
    modifier = modifier.heightIn(min = 48.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = if (containerColor == MaterialTheme.colorScheme.primary) Color.White else MaterialTheme.colorScheme.onPrimary
    ),
    shape = RoundedCornerShape(12.dp),
    contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
  ) {
    if (icon != null) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
    }
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.5.sp),
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
  }
}


@Composable
fun AsliIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 40.dp
) {
    Surface(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

@Composable
fun GrayButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.heightIn(min = 48.dp),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
    contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
  ) { 
    Text(
        text = text, 
        fontWeight = FontWeight.Bold, 
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    ) 
  }
}


@Composable
fun PremiumBanner(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier.padding(AppSpacing.lg),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Outlined.MilitaryTech, contentDescription = null, tint = Color.White)
      }
      
      Column(modifier = Modifier.weight(1f)) {
        Text(
          "Upgrade to Premium",
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          "Get more features and support",
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
          style = MaterialTheme.typography.bodySmall
        )
      }

      Icon(
        imageVector = Icons.Outlined.MilitaryTech,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
fun AsliTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null

) {
    val isPassword = keyboardOptions.keyboardType == KeyboardType.Password
    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,

        trailingIcon = {
            if (isPassword) {
                val icon = if (passwordVisible.value) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                trailingIcon?.invoke()
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            cursorColor = MaterialTheme.colorScheme.primary,
            selectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}


@Composable
fun GlassButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = text, 
            color = MaterialTheme.colorScheme.primary, 
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BillingTotalCard(
    totalItems: Int,
    grandTotal: Double,
    modifier: Modifier = Modifier
) {
    DarkCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = AppSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Total Items", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    totalItems.toString(), 
                    color = MaterialTheme.colorScheme.onSurface, 
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Grand Total", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "₹ ${grandTotal.toInt()}", 
                    color = MaterialTheme.colorScheme.primary, 
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    onSelected: () -> Unit = {},
    onClick: (() -> Unit)? = null, // Compatibility
    modifier: Modifier = Modifier
) {
    val actualOnClick = onClick ?: onSelected
    Surface(
        modifier = modifier.clickable { actualOnClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DateBox(
    label: String,
    date: String = "",
    value: String? = null, // Compatibility
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayDate = value ?: date
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayDate, 
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun openDatePicker(context: Context, initialDate: Any? = null, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    if (initialDate is Long) {
        calendar.timeInMillis = initialDate
    }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val resultCalendar = Calendar.getInstance()
            resultCalendar.set(year, month, day)
            onDateSelected(resultCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// Fixed version to handle any number of arguments from various screens
fun openDatePicker(context: Context, a: Any?, b: Any?, c: Any?, onDateSelected: (Long) -> Unit) {
    openDatePicker(context, a, onDateSelected)
}

@Composable
fun CircularKey(
    text: String = "",
    label: String? = null, // Compatibility
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAction: Boolean = false,
    containerColor: Color? = null
) {
    val displayText = label ?: text
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = CircleShape,
        color = containerColor ?: if (isAction) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isAction || containerColor != null) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            } else {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isAction || containerColor != null) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun PremiumSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AsliTable(
    headers: List<String>,
    columnWeights: List<Float>,
    isEmpty: Boolean,
    emptyIcon: ImageVector = Icons.Outlined.ShoppingCart,
    emptyText: String = "Cart is empty",
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val headerBg = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)
    val bodyBg = if (isDark) Color(0xFF111827) else Color.White
    val headerText = if (isDark) Color(0xFF93C5FD) else Color(0xFF2563EB)
    val emptyIconTint = if (isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
    val emptyTextColor = if (isDark) Color(0xFF334155) else Color(0xFF64748B)

    Surface(
        modifier = modifier.shadow(
            elevation = if (isDark) 8.dp else 4.dp,
            shape = RoundedCornerShape(24.dp),
            spotColor = Color.Black.copy(alpha = if (isDark) 0.5f else 0.1f)
        ),
        color = bgColor,
        shape = RoundedCornerShape(24.dp),
        border = if (!isDark) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                headers.forEachIndexed { index, title ->
                    Text(
                        text = title.uppercase(),
                        modifier = Modifier.weight(columnWeights.getOrElse(index) { 1f }),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = headerText,
                            letterSpacing = 1.2.sp
                        ),
                        textAlign = if (index == headers.lastIndex) TextAlign.End else TextAlign.Start
                    )
                }
            }

            // Body
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bodyBg)
            ) {
                if (isEmpty) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = emptyIcon,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = emptyIconTint
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = emptyText,
                            style = MaterialTheme.typography.titleMedium,
                            color = emptyTextColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
