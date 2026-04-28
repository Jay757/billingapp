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
    private val tag = "BackendHealthMonitor"

    fun start(onlineIntervalMs: Long = 60_000, offlineIntervalMs: Long = 5_000) {
        if (monitorJob != null) return
        
        monitorJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val isOnline = client.checkHealth()
                    val currentStatus = repository.isOnline.value
                    
                    if (isOnline != currentStatus) {
                        Log.d(tag, "Backend status changed: Online=$isOnline")
                        repository.updateStatus(isOnline, if (isOnline) null else "Backend unreachable")
                    }
                    
                    // Use a shorter delay if we're offline to reconnect faster
                    val nextDelay = if (isOnline) onlineIntervalMs else offlineIntervalMs
                    delay(nextDelay)
                } catch (e: Exception) {
                    Log.e(tag, "Health check failed", e)
                    repository.updateStatus(false, e.message)
                    delay(offlineIntervalMs)
                }
            }
        }
    }

    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
    }

    /**
     * Force an immediate health check.
     */
    fun checkNow() {
        scope.launch(Dispatchers.IO) {
            val isOnline = client.checkHealth()
            repository.updateStatus(isOnline, if (isOnline) null else "Backend unreachable")
        }
    }
}
