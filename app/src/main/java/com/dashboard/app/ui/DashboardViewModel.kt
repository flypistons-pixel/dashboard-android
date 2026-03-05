package com.dashboard.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dashboard.app.data.DashboardRepository
import com.dashboard.app.model.*
import kotlinx.coroutines.*

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    val repo = DashboardRepository(app)

    val metrics    = MutableLiveData<Metrics>()
    val history    = MutableLiveData<List<HistoryPoint>>()
    val categories = MutableLiveData<List<CategoryItem>>()
    val tableRows  = MutableLiveData<List<TableRow>>()
    val error      = MutableLiveData<String?>()
    val loading    = MutableLiveData(false)

    private var refreshJob: Job? = null

    fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                fetchAll()
                delay(repo.refreshInterval * 1000L)
            }
        }
    }

    fun stopAutoRefresh() { refreshJob?.cancel() }

    fun refreshNow() { viewModelScope.launch { fetchAll() } }

    private suspend fun fetchAll() {
        loading.postValue(true)
        error.postValue(null)
        try {
            coroutineScope {
                val m = async { repo.fetchMetrics() }
                val h = async { repo.fetchHistory() }
                val c = async { repo.fetchCategory() }
                val t = async { repo.fetchTable() }
                metrics.postValue(m.await())
                history.postValue(h.await())
                categories.postValue(c.await())
                tableRows.postValue(t.await())
            }
        } catch (e: Exception) {
            error.postValue("Error de conexión: ${e.message}")
        } finally {
            loading.postValue(false)
        }
    }
}
