package com.dashboard.app.data

import android.content.Context
import android.content.SharedPreferences
import com.dashboard.app.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class DashboardRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dashboard_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    var serverUrl: String
        get() = prefs.getString("server_url", "http://192.168.1.100:5000") ?: "http://192.168.1.100:5000"
        set(value) = prefs.edit().putString("server_url", value.trimEnd('/')).apply()

    var refreshInterval: Int
        get() = prefs.getInt("refresh_interval", 5)
        set(value) = prefs.edit().putInt("refresh_interval", value).apply()

    private suspend fun get(path: String): String = withContext(Dispatchers.IO) {
        val req = Request.Builder().url("$serverUrl$path").build()
        client.newCall(req).execute().use { it.body!!.string() }
    }

    suspend fun fetchMetrics(): Metrics = gson.fromJson(get("/api/metrics"), Metrics::class.java)

    suspend fun fetchHistory(): List<HistoryPoint> {
        val type = object : TypeToken<List<HistoryPoint>>() {}.type
        return gson.fromJson(get("/api/history"), type)
    }

    suspend fun fetchCategory(): List<CategoryItem> {
        val type = object : TypeToken<List<CategoryItem>>() {}.type
        return gson.fromJson(get("/api/category"), type)
    }

    suspend fun fetchTable(): List<TableRow> {
        val type = object : TypeToken<List<TableRow>>() {}.type
        return gson.fromJson(get("/api/table"), type)
    }
}
