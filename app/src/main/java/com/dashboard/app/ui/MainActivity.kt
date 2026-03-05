package com.dashboard.app.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashboard.app.R
import com.dashboard.app.databinding.ActivityMainBinding
import com.dashboard.app.model.CategoryItem
import com.dashboard.app.model.HistoryPoint
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: DashboardViewModel by viewModels()
    private lateinit var tableAdapter: TableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupCharts()
        setupTable()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener { vm.refreshNow() }
        vm.startAutoRefresh()
    }

    override fun onPause()  { super.onPause();  vm.stopAutoRefresh() }
    override fun onResume() { super.onResume(); vm.startAutoRefresh() }

    // ── MENU ──────────────────────────────────────────────────
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ── CHARTS SETUP ──────────────────────────────────────────
    private fun setupCharts() {
        // Line chart
        binding.lineChart.apply {
            setBackgroundColor(Color.TRANSPARENT)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            legend.textColor = Color.parseColor("#64748b")
            legend.textSize = 10f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.parseColor("#64748b")
            xAxis.gridColor = Color.parseColor("#1e2d45")
            xAxis.axisLineColor = Color.parseColor("#1e2d45")
            axisLeft.textColor = Color.parseColor("#64748b")
            axisLeft.gridColor = Color.parseColor("#1e2d45")
            axisLeft.axisLineColor = Color.parseColor("#1e2d45")
            axisRight.isEnabled = false
            setNoDataTextColor(Color.parseColor("#64748b"))
            setNoDataText("Conectando al servidor...")
        }

        // Bar chart
        binding.barChart.apply {
            setBackgroundColor(Color.TRANSPARENT)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.parseColor("#64748b")
            xAxis.gridColor = Color.TRANSPARENT
            xAxis.axisLineColor = Color.parseColor("#1e2d45")
            axisLeft.textColor = Color.parseColor("#64748b")
            axisLeft.gridColor = Color.parseColor("#1e2d45")
            axisLeft.axisLineColor = Color.parseColor("#1e2d45")
            axisRight.isEnabled = false
            setNoDataTextColor(Color.parseColor("#64748b"))
            setNoDataText("Sin datos")
        }
    }

    private fun setupTable() {
        tableAdapter = TableAdapter()
        binding.recyclerTable.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tableAdapter
            isNestedScrollingEnabled = false
        }
    }

    // ── OBSERVERS ─────────────────────────────────────────────
    private fun observeViewModel() {
        vm.loading.observe(this) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        vm.error.observe(this) { msg ->
            if (msg != null) {
                binding.errorBanner.visibility = View.VISIBLE
                binding.errorText.text = msg
            } else {
                binding.errorBanner.visibility = View.GONE
            }
        }

        vm.metrics.observe(this) { m ->
            binding.kpiCpuValue.text  = m.cpu
            binding.kpiMemValue.text  = m.mem
            binding.kpiNetValue.text  = "${m.net} MB/s"
            binding.kpiAlertsValue.text = "${m.alerts}"

            binding.kpiCpuDelta.text  = "${m.cpu_delta}%"
            binding.kpiMemDelta.text  = "${m.mem_delta}%"

            val alertColor = if (m.alerts > 0)
                Color.parseColor("#ff4d6d") else Color.parseColor("#00f5c4")
            binding.kpiAlertsValue.setTextColor(alertColor)
        }

        vm.history.observe(this) { pts -> updateLineChart(pts) }
        vm.categories.observe(this) { cats -> updateBarChart(cats) }
        vm.tableRows.observe(this) { rows -> tableAdapter.submitList(rows) }
    }

    // ── CHART UPDATES ─────────────────────────────────────────
    private fun updateLineChart(pts: List<HistoryPoint>) {
        val entriesA = pts.mapIndexed { i, p -> Entry(i.toFloat(), p.a.toFloat()) }
        val entriesB = pts.mapIndexed { i, p -> Entry(i.toFloat(), p.b.toFloat()) }

        val dsA = LineDataSet(entriesA, "Serie A").apply {
            color = Color.parseColor("#00f5c4")
            setDrawCircles(false); lineWidth = 2f
            fillAlpha = 30; fillColor = Color.parseColor("#00f5c4")
            setDrawFilled(true); mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 0f
        }
        val dsB = LineDataSet(entriesB, "Serie B").apply {
            color = Color.parseColor("#7c6ff7")
            setDrawCircles(false); lineWidth = 2f
            fillAlpha = 30; fillColor = Color.parseColor("#7c6ff7")
            setDrawFilled(true); mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 0f
        }

        binding.lineChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(pts.map { it.t })
        binding.lineChart.data = LineData(dsA, dsB)
        binding.lineChart.invalidate()
    }

    private fun updateBarChart(cats: List<CategoryItem>) {
        val barColors = listOf(
            "#00f5c4", "#7c6ff7", "#ff4d6d",
            "#ffd166", "#00b4d8", "#e76f51"
        ).map { Color.parseColor(it) }

        val entries = cats.mapIndexed { i, c -> BarEntry(i.toFloat(), c.value.toFloat()) }
        val ds = BarDataSet(entries, "").apply {
            colors = barColors.take(entries.size).let {
                if (it.size < entries.size) it + List(entries.size - it.size) { barColors[0] } else it
            }
            valueTextColor = Color.parseColor("#64748b")
            valueTextSize = 9f
        }

        binding.barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(cats.map { it.name })
        binding.barChart.xAxis.labelCount = cats.size
        binding.barChart.data = BarData(ds).apply { barWidth = 0.6f }
        binding.barChart.invalidate()
    }
}
