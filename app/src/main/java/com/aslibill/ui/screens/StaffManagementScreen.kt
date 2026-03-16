package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.StaffEntity
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
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
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Staff Management", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
        OrangeButton("+ Add", onClick = { showAdd = true })
      }

      SectionHeader("All Staff (${staffList.size})")

      if (staffList.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
          Text("No staff added yet. Tap '+ Add' to begin.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(12.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(staff.name, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Text(staff.role, color = AsliColors.Orange, style = MaterialTheme.typography.labelMedium)
        Text(staff.mobile, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
      }
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        val activeBg = if (staff.isActive) AsliColors.Green else AsliColors.Card2
        Box(
          modifier = Modifier
            .background(activeBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            if (staff.isActive) "ACTIVE" else "INACTIVE",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
          )
        }
        IconButton(onClick = onEdit) {
          Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.TextSecondary)
        }
        IconButton(onClick = onDelete) {
          Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red)
        }
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

  val fieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AsliColors.Orange,
    unfocusedBorderColor = AsliColors.TextSecondary,
    focusedLabelColor = AsliColors.Orange,
    unfocusedLabelColor = AsliColors.TextSecondary,
    focusedTextColor = AsliColors.TextPrimary,
    unfocusedTextColor = AsliColors.TextPrimary,
    cursorColor = AsliColors.Orange
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title, color = AsliColors.TextPrimary) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (e.g. Manager)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      TextButton(onClick = {
        if (name.isNotBlank()) onConfirm(name, role, mobile)
      }) { Text("SAVE", color = AsliColors.Orange) }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } }
  )
}
