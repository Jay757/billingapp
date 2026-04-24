package com.aslibill.network

import android.util.Log
import com.aslibill.data.NetworkStatusRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BackendHealthMonitor(
    private val client: ApiHttpClient,
    private val repository: NetworkStatusRepository,
    private val scope: CoroutineScope
) {
    private var monitorJob: Job? = null

    fun start(intervalMs: Long = 86_400_000) {
        if (monitorJob != null) return
        
        monitorJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val isOnline = client.checkHealth()
                val currentStatus = repository.isOnline.value
                
                if (isOnline != currentStatus) {
                    repository.updateStatus(isOnline, if (isOnline) null else "Backend unreachable")
                }
                
                delay(intervalMs)
            }
        }
    }

    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
    }
}
