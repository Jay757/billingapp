package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrinterProduct(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val imageUrl: String = "" // Placeholder
)

class BuyPrintersViewModel : ViewModel() {
    private val _printers = MutableStateFlow(
        listOf(
            PrinterProduct(
                "1",
                "58mm Bluetooth Thermal Printer",
                "Portable, wireless, 2-inch receipt printer. Ideal for mobile billing.",
                "₹2,499"
            ),
            PrinterProduct(
                "2",
                "80mm Desktop Thermal Printer",
                "High-speed, 3-inch receipt printer with USB/LAN/Bluetooth. Perfect for counters.",
                "₹4,999"
            ),
            PrinterProduct(
                "3",
                "Barcode/Label Printer",
                "Print high-quality barcode stickers and price tags for your products.",
                "₹6,500"
            ),
            PrinterProduct(
                "4",
                "Billing Roll (Pack of 10)",
                "High-quality 58mm thermal paper rolls for your receipt printers.",
                "₹450"
            )
        )
    )
    val printers: StateFlow<List<PrinterProduct>> = _printers.asStateFlow()

    fun buyPrinter(printer: PrinterProduct) {
        // In a real app, this would redirect to an e-commerce site or contact support
        println("User wants to buy: ${printer.name}")
    }
}
