package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ContactInfo(
    val phone: String = "+91 9207080178",
    val email: String = "support@aslibill.com",
    val workingHours: String = "Mon-Sat, 9 AM - 6 PM"
)

class ContactUsViewModel : ViewModel() {
    private val _contactInfo = MutableStateFlow(ContactInfo())
    val contactInfo: StateFlow<ContactInfo> = _contactInfo.asStateFlow()

    fun callSupport() {
        // In a real app, this would trigger a dialer intent
        println("Calling Support: ${_contactInfo.value.phone}")
    }

    fun whatsappSupport() {
        // In a real app, this would trigger a WhatsApp intent
        println("WhatsApp Support: ${_contactInfo.value.phone}")
    }

    fun emailSupport() {
        // In a real app, this would trigger an email intent
        println("Emailing Support: ${_contactInfo.value.email}")
    }
}
