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
fun DarkCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = AsliColors.Card),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
    colors = ButtonDefaults.buttonColors(containerColor = AsliColors.Card2, contentColor = AsliColors.TextPrimary),
    shape = RoundedCornerShape(10.dp),
    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
  ) { Text(text, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis) }
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
      .clip(RoundedCornerShape(20.dp))
      .background(bg)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text, color = fg, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
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
