package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ContactInfo(
    val phone: String = "+91 9586028425",
    val email: String = "jaymungara757@gamil.com",
    val workingHours: String = "Mon-Sat, 9 AM - 6 PM"
)

class ContactUsViewModel : ViewModel() {
    private val _contactInfo = MutableStateFlow(ContactInfo())
    val contactInfo: StateFlow<ContactInfo> = _contactInfo.asStateFlow()

    fun callSupport() {
    }

    fun whatsappSupport() {
    }

    fun emailSupport() {
    }
}

