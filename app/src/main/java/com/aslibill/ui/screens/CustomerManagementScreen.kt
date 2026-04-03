package com.aslibill.ui.screens

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
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CustomerEntity
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AsliColors

@Composable
fun CustomerManagementScreen(
  contentPadding: PaddingValues,
  vm: CustomerManagementViewModel
) {
  val customers by vm.customers.collectAsState()
  var showAdd by remember { mutableStateOf(false) }
  var editTarget by remember { mutableStateOf<CustomerEntity?>(null) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Customers", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
        OrangeButton("+ Add", onClick = { showAdd = true })
      }

      SectionHeader("All Customers (${customers.size})")

      if (customers.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
          Text("No customers yet. Tap '+ Add' to begin.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
          items(customers, key = { it.id }) { customer ->
            CustomerCard(
              customer = customer,
              onEdit = { editTarget = customer },
              onDelete = { vm.deleteCustomer(customer) }
            )
          }
        }
      }
    }

    if (showAdd) {
      CustomerDialog(
        title = "Add Customer",
        initial = CustomerEntity(name = "", mobile = ""),
        onConfirm = { name, mobile, address ->
          vm.addCustomer(name, mobile, address)
          showAdd = false
        },
        onDismiss = { showAdd = false }
      )
    }

    editTarget?.let { customer ->
      CustomerDialog(
        title = "Edit Customer",
        initial = customer,
        onConfirm = { name, mobile, address ->
          vm.updateCustomer(customer, name, mobile, address)
          editTarget = null
        },
        onDismiss = { editTarget = null }
      )
    }
  }
}

@Composable
private fun CustomerCard(
  customer: CustomerEntity,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(12.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(customer.name, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Text(customer.mobile, color = AsliColors.Orange, style = MaterialTheme.typography.labelMedium)
        if (!customer.address.isNullOrBlank()) {
          Text(customer.address, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
      }
      Row {
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
private fun CustomerDialog(
  title: String,
  initial: CustomerEntity,
  onConfirm: (String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(initial.name) }
  var mobile by remember { mutableStateOf(initial.mobile) }
  var address by remember { mutableStateOf(initial.address ?: "") }

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
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address (optional)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      TextButton(onClick = {
        if (name.isNotBlank() && mobile.isNotBlank()) onConfirm(name, mobile, address)
      }) { Text("SAVE", color = AsliColors.Orange) }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } }
  )
}
