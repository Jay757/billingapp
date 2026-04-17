package com.aslibill.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.Brand
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ScreenSurface(content: @Composable () -> Unit) {
  Surface(color = MaterialTheme.colorScheme.background, content = content)
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
      color = AsliColors.TextPrimary
    )
    Spacer(Modifier.height(AppSpacing.sm))
    Box(
      Modifier
        .fillMaxWidth()
        .height(2.dp)
        .background(AsliColors.DividerOrange)
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
      elevation = 8.dp,
      shape = RoundedCornerShape(16.dp),
      ambientColor = AsliColors.Primary.copy(alpha = 0.1f),
      spotColor = AsliColors.Primary.copy(alpha = 0.2f)
    ),
    colors = CardDefaults.cardColors(
      containerColor = AsliColors.Card.copy(alpha = alpha)
    ),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = AsliColors.Card2.copy(alpha = 0.5f)
    )
  ) { content() }
}

@Composable
fun IconTile(
  label: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconSize: Dp = 40.dp
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(AsliColors.Card2)
      .clickable(onClick = onClick)
      .padding(vertical = AppSpacing.lg, horizontal = AppSpacing.md),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(AsliColors.PrimaryLight)
        .padding(12.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = AsliColors.Primary, modifier = Modifier.height(iconSize))
    }
    Text(
      text = label,
      color = AsliColors.TextPrimary,
      style = MaterialTheme.typography.labelMedium,
      maxLines = 3,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun OrangeButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    onClick = onClick,
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(containerColor = AsliColors.Primary, contentColor = Color.White),
    shape = RoundedCornerShape(10.dp),
    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
  ) { Text(text, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis) }
}


@Composable
fun GrayButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    onClick = onClick,
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = AsliColors.TextPrimary),
    shape = RoundedCornerShape(10.dp),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
  ) { Text(text, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis) }
}

@Composable
fun Chip(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg = if (selected) AsliColors.Primary else AsliColors.Card2
  val fg = if (selected) Color.White else AsliColors.TextSecondary
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bg)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text, color = fg, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
  }
}

@Composable
fun PremiumSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AsliColors.Card2.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        val transition = updateTransition(selectedIndex, label = "SelectedTab")
        val indicatorOffset by transition.animateDp(
            transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
            label = "IndicatorOffset"
        ) { index ->
            // This is a bit simplified, but works for 2 tabs. 
            // For N tabs, we'd need more logic. 
            if (index == 0) 0.dp else 1.dp // We'll handle this in the BoxWithConstraints below
            0.dp 
        }

        androidx.compose.foundation.layout.BoxWithConstraints {
            val tabWidth = maxWidth / options.size
            val offset by transition.animateDp(
                transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy) },
                label = "Offset"
            ) { tabWidth * it }

            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .fillMaxHeight()
                    .width(tabWidth)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AsliColors.Primary, AsliColors.Primary.copy(alpha = 0.8f))
                        )
                    )
                    .shadow(4.dp, RoundedCornerShape(20.dp))
            )

            Row(modifier = Modifier.fillMaxSize()) {
                options.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { onOptionSelected(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedIndex == index) Color.White else AsliColors.TextSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = AsliColors.Card.copy(alpha = 0.7f)
) {
    Surface(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = AsliColors.Primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun CircularKey(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(AsliColors.Card2)
      .clickable(onClick = onClick)
      .padding(18.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(label, color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineSmall)
  }
}

@Composable
fun AsliTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val isPassword = keyboardOptions.keyboardType == KeyboardType.Password
    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (isPassword) {
                val icon = if (passwordVisible.value) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(icon, contentDescription = if (passwordVisible.value) "Hide password" else "Show password", tint = AsliColors.TextSecondary)
                }
            } else {
                trailingIcon?.invoke()
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = AsliColors.Orange,
            unfocusedIndicatorColor = AsliColors.TextSecondary,
            focusedTextColor = AsliColors.TextPrimary,
            unfocusedTextColor = AsliColors.TextPrimary,
            focusedLabelColor = AsliColors.Orange,
            unfocusedLabelColor = AsliColors.TextSecondary
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
    )
}

@Composable
fun DateBox(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  DarkCard(modifier = modifier.clickable(onClick = onClick)) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(label, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = AsliColors.TextSecondary)
        Text(value, color = AsliColors.TextPrimary)
      }
    }
  }
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = AsliColors.Primary,
    modifier: Modifier = Modifier
) {
    DarkCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(label, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
                Text(value, color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun PremiumBanner(onClick: () -> Unit, modifier: Modifier = Modifier) {
  DarkCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier
        .padding(AppSpacing.md),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(AsliColors.Orange),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("NOVA", color = Color.Black, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold))
          Text("BILL", color = Color.Black, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold))
        }
      }
      
      Column(modifier = Modifier.weight(1f)) {
        Text(
          "Upgrade to ${Brand.AppName}",
          color = AsliColors.TextPrimary,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          "Premium",
          color = AsliColors.TextPrimary,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      Icon(
        imageVector = Icons.Outlined.MilitaryTech,
        contentDescription = null,
        tint = AsliColors.Orange,
        modifier = Modifier.size(32.dp)
      )
    }
  }
}

fun openDatePicker(context: Context, initialEpochMs: Long, onPicked: (Long) -> Unit) {
  val cal = Calendar.getInstance().apply {
    timeInMillis = initialEpochMs
  }
  DatePickerDialog(
    context,
    { _, year, month, day ->
      val c = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
      }
      onPicked(c.timeInMillis)
    },
    cal.get(Calendar.YEAR),
    cal.get(Calendar.MONTH),
    cal.get(Calendar.DAY_OF_MONTH)
  ).show()
}
