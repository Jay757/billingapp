package com.aslibill.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CustomerEntity
import com.aslibill.ui.components.AsliIconButton
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors

@Composable
fun CustomerManagementScreen(
  contentPadding: PaddingValues,
  vm: CustomerManagementViewModel
) {
  val customers by vm.customers.collectAsState()
  val isLoading by vm.isLoading.collectAsState()
  var showAdd by remember { mutableStateOf(false) }
  var editTarget by remember { mutableStateOf<CustomerEntity?>(null) }

  ScreenSurface {
    Box(modifier = Modifier.fillMaxSize()) {
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
          "Customers",
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
        )

        OrangeButton(
          text = "Add Customer",
          icon = Icons.Default.Add,
          onClick = { showAdd = true }
        )
      }

      SectionHeader("ALL REGISTERED CUSTOMERS")

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

    if (isLoading) {
      com.aslibill.ui.components.AsliLoader()
    }
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
      modifier = Modifier
        .padding(AppSpacing.lg)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          customer.name.uppercase(),
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          customer.mobile,
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(top = 2.dp)
        )

        if (!customer.address.isNullOrBlank()) {
          Text(
            customer.address,
            color = AsliColors.TextSecondary,
            style = AppTypography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        AsliIconButton(
          icon = Icons.Outlined.Edit,
          onClick = onEdit,
          containerColor = AsliColors.PrimaryBlue.copy(alpha = 0.1f),
          contentColor = AsliColors.PrimaryBlue
        )
        AsliIconButton(
          icon = Icons.Outlined.Delete,
          onClick = onDelete,
          containerColor = AsliColors.Red.copy(alpha = 0.1f),
          contentColor = AsliColors.Red
        )
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


  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title, style = MaterialTheme.typography.titleLarge) },
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        com.aslibill.ui.components.AsliTextField(value = name, onValueChange = { name = it }, label = "Name")
        com.aslibill.ui.components.AsliTextField(value = mobile, onValueChange = { mobile = it }, label = "Mobile")
        com.aslibill.ui.components.AsliTextField(value = address, onValueChange = { address = it }, label = "Address (optional)")
      }
    },
    confirmButton = {
      TextButton(onClick = {
        if (name.isNotBlank() && mobile.isNotBlank()) onConfirm(name, mobile, address)
      }) { Text("SAVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}

