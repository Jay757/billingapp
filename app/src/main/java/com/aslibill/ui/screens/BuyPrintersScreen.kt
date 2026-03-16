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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AsliColors

@Composable
fun BuyPrintersScreen(
    contentPadding: PaddingValues,
    vm: BuyPrintersViewModel
) {
    val printers by vm.printers.collectAsState()

    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader("Buy Compatible Printers")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(printers) { printer ->
                    PrinterItem(printer = printer, onBuyClick = { vm.buyPrinter(printer) })
                }
            }
        }
    }
}

@Composable
private fun PrinterItem(printer: PrinterProduct, onBuyClick: () -> Unit) {
    DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for Printer Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AsliColors.Card2),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Print,
                        contentDescription = null,
                        tint = AsliColors.Orange,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        printer.name,
                        color = AsliColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        printer.price,
                        color = AsliColors.Orange,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(
                printer.description,
                color = AsliColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            OrangeButton(
                "BUY NOW",
                onClick = onBuyClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
