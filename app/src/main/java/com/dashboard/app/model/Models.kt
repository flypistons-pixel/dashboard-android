package com.dashboard.app.model

data class Metrics(
    val cpu: String = "—",
    val cpu_delta: Double = 0.0,
    val mem: String = "—",
    val mem_delta: Double = 0.0,
    val net: Double = 0.0,
    val alerts: Int = 0
)

data class HistoryPoint(
    val t: String = "",
    val a: Double = 0.0,
    val b: Double = 0.0
)

data class CategoryItem(
    val name: String = "",
    val value: Double = 0.0
)

data class TableRow(
    val id: String = "",
    val name: String = "",
    val value: Double = 0.0,
    val status: String = "ok"   // "ok" | "warn" | "err"
)
