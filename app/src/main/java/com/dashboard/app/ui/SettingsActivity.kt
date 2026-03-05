package com.dashboard.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dashboard.app.data.DashboardRepository
import com.dashboard.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var repo: DashboardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply { title = "Configuración"; setDisplayHomeAsUpEnabled(true) }

        repo = DashboardRepository(this)

        binding.etServerUrl.setText(repo.serverUrl)
        binding.etRefreshInterval.setText(repo.refreshInterval.toString())

        binding.btnSave.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim()
            val interval = binding.etRefreshInterval.text.toString().toIntOrNull() ?: 5

            if (url.isEmpty() || (!url.startsWith("http://") && !url.startsWith("https://"))) {
                binding.etServerUrl.error = "URL inválida (ej: http://192.168.1.100:5000)"
                return@setOnClickListener
            }
            if (interval < 1 || interval > 60) {
                binding.etRefreshInterval.error = "Intervalo entre 1 y 60 segundos"
                return@setOnClickListener
            }

            repo.serverUrl = url
            repo.refreshInterval = interval
            Toast.makeText(this, "✓ Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
