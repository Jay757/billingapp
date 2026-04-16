package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.db.BillDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class HomeViewModel(billDao: BillDao) : ViewModel() {

    private val startOfDay: Long
    private val endOfDay: Long

    init {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        startOfDay = cal.timeInMillis
        endOfDay = startOfDay + 24L * 60 * 60 * 1_000 - 1
    }

    private val todayFlow = billDao.observeBillsBetween(startOfDay, endOfDay)

    val todaySalesTotal: StateFlow<Double> = todayFlow
        .map { bills -> bills.sumOf { it.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val todayBillCount: StateFlow<Int> = todayFlow
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

class HomeViewModelFactory(private val billDao: BillDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(billDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
