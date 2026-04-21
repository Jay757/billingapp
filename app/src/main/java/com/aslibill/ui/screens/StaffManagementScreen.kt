package com.aslibill.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.StaffEntity
import com.aslibill.ui.components.AsliIconButton
import com.aslibill.ui.components.AsliTextField

import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors

@Composable
fun StaffManagementScreen(
  contentPadding: PaddingValues,
  vm: StaffManagementViewModel
) {
  val staffList by vm.staffList.collectAsState()
  var showAdd by remember { mutableStateOf(false) }
  var editTarget by remember { mutableStateOf<StaffEntity?>(null) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(horizontal = AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = AppSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Staff Management",
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
        )

        OrangeButton(
          text = "Add Staff",
          icon = Icons.Default.Add,
          onClick = { showAdd = true }
        )
      }

      SectionHeader("ALL ACTIVE STAFF")

      if (staffList.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
          Text("No staff added yet. Tap '+ Add' to begin.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
          items(staffList, key = { it.id }) { staff ->
            StaffCard(
              staff = staff,
              onEdit = { editTarget = staff },
              onToggle = { vm.toggleActive(staff) },
              onDelete = { vm.deleteStaff(staff) }
            )
          }
        }
      }
    }

    if (showAdd) {
      StaffDialog(
        title = "Add Staff",
        initial = StaffEntity(name = "", role = "", mobile = ""),
        onConfirm = { name, role, mobile ->
          vm.addStaff(name, role, mobile)
          showAdd = false
        },
        onDismiss = { showAdd = false }
      )
    }

    editTarget?.let { staff ->
      StaffDialog(
        title = "Edit Staff",
        initial = staff,
        onConfirm = { name, role, mobile ->
          vm.updateStaff(staff, name, role, mobile)
          editTarget = null
        },
        onDismiss = { editTarget = null }
      )
    }
  }
}

@Composable
private fun StaffCard(
  staff: StaffEntity,
  onEdit: () -> Unit,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  DarkCard(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onToggle() }
  ) {
    Row(
      modifier = Modifier
        .padding(AppSpacing.lg)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          staff.name.uppercase(),
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          staff.role,
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(top = 2.dp)
        )

        Text(
          staff.mobile,
          color = AsliColors.TextSecondary,
          style = AppTypography.bodySmall,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
      
      Row(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status Badge
        Surface(
          color = if (staff.isActive) AsliColors.SuccessGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            if (staff.isActive) "ACTIVE" else "INACTIVE",
            color = if (staff.isActive) AsliColors.SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }


        AsliIconButton(
          icon = Icons.Outlined.Edit,
          onClick = onEdit,
          containerColor = AsliColors.PrimaryBlue.copy(alpha = 0.1f),
          contentColor = AsliColors.PrimaryBlue
        )
        
        AsliIconButton(
          icon = Icons.Outlined.Delete,
          onClick = onDelete,
          containerColor = AsliColors.AlertOrange.copy(alpha = 0.1f),
          contentColor = AsliColors.AlertOrange
        )
      }
    }
  }
}

@Composable
private fun StaffDialog(
  title: String,
  initial: StaffEntity,
  onConfirm: (String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(initial.name) }
  var role by remember { mutableStateOf(initial.role) }
  var mobile by remember { mutableStateOf(initial.mobile) }


  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title, style = MaterialTheme.typography.titleLarge) },
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AsliTextField(value = name, onValueChange = { name = it }, label = "Name")
        AsliTextField(value = role, onValueChange = { role = it }, label = "Role (e.g. Manager)")
        AsliTextField(value = mobile, onValueChange = { mobile = it }, label = "Mobile")
      }
    },
    confirmButton = {
      TextButton(onClick = {
        if (name.isNotBlank()) onConfirm(name, role, mobile)
      }) { Text("SAVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}

