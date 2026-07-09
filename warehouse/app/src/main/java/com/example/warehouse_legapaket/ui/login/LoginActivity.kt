package com.example.warehouse_legapaket.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse_legapaket.MainActivity
import com.example.warehouse_legapaket.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Jalankan Auto Login otomatis jika sesi aktif masih tersimpan
        val sharedPref = getSharedPreferences("SesiLegaPaket", Context.MODE_PRIVATE)
        val isLogin = sharedPref.getBoolean("IS_LOGIN", false)
        if (isLogin) {
            bukaMainActivity()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Fungsi pembantu untuk memvalidasi kedua input secara bersamaan
        fun validasiInputRealTime() {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val emailValid = email.contains("@warehouse.com")
            val passwordValid = password.length >= 6

            // Tombol hanya aktif jika email mengandung @warehouse.com DAN password minimal 6 karakter
            btnLogin.isEnabled = emailValid && passwordValid
        }

        // 2. VALIDASI REAL-TIME EMAIL
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val emailInput = s.toString().trim()
                if (emailInput.isNotEmpty() && !emailInput.contains("@warehouse.com")) {
                    etEmail.error = "Wajib menggunakan email @warehouse.com"
                } else {
                    etEmail.error = null
                }
                validasiInputRealTime()
            }
        })

        // 3. VALIDASI REAL-TIME PASSWORD (Minimal 6 Karakter)
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val passwordInput = s.toString().trim()
                if (passwordInput.isNotEmpty() && passwordInput.length < 6) {
                    etPassword.error = "Password minimal harus 6 karakter"
                } else {
                    etPassword.error = null
                }
                validasiInputRealTime()
            }
        })

        // Jalankan pengecekan awal agar tombol mati saat pertama kali aplikasi dibuka (karena input masih kosong)
        validasiInputRealTime()

        // 4. Logika klik tombol login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email == "revan.admin@warehouse.com" && password == "admin123") {
                simpanSesiLogin("admin_pusat")
                Toast.makeText(this, "Selamat datang, Admin Pusat!", Toast.LENGTH_SHORT).show()
                bukaMainActivity()
            } else if (email == "ahmad.sortir@warehouse.com" && password == "staff123") {
                simpanSesiLogin("staff_sortir")
                Toast.makeText(this, "Selamat datang, Staff Sortir!", Toast.LENGTH_SHORT).show()
                bukaMainActivity()
            } else {
                Toast.makeText(this, "Email atau Password Anda salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpanSesiLogin(usernameRole: String) {
        val sharedPref = getSharedPreferences("SesiLegaPaket", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("IS_LOGIN", true)
        editor.putString("USERNAME_LOGIN", usernameRole)
        editor.apply()
    }

    private fun bukaMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}