package com.example.warehouse_legapaket.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.warehouse_legapaket.ui.login.LoginActivity
import com.example.warehouse_legapaket.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionListeners()
        loadUserProfileData()
    }

    private fun setupActionListeners() {
//        binding.btnChangePassword.setOnClickListener {
//            Toast.makeText(requireContext(), "Menu Ganti Kata Sandi dibuka", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.btnNotificationSetting.setOnClickListener {
//            Toast.makeText(requireContext(), "Pengaturan notifikasi dibuka", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.btnHelp.setOnClickListener {
//            Toast.makeText(requireContext(), "Pusat Bantuan Lega Paket dibuka", Toast.LENGTH_SHORT).show()
//        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserProfileData() {
        if (_binding == null) return

        val sharedPref = activity?.getSharedPreferences("SesiLegaPaket", Context.MODE_PRIVATE)
        val usernameAktif = sharedPref?.getString("USERNAME_LOGIN", "admin_pusat")

        // Injeksi data dinamis berdasarkan ID XML Anda yang asli secara aman (null-safe)
        if (usernameAktif == "staff_sortir") {
            binding.tvProfileName.text = "Ahmad Sulaeman"
            binding.tvProfileRole.text = "Staff Sortir Gudang"
            binding.tvEmail.text = "ahmad.sortir@warehouse.com"
            binding.tvPhone.text = "+62 812-9988-1122"
            binding.tvEmployeeId.text = "LP-STR-2026-041"
            binding.tvLocation.text = "📍 Bandung Sorting Hub"
            binding.tvShift.text = "⏰ Malam (16:00 - 24:00)"
        } else {
            binding.tvProfileName.text = "Revan Warehouse"
            binding.tvProfileRole.text = "Admin Warehouse Pusat"
            binding.tvEmail.text = "revan.admin@warehouse.com"
            binding.tvPhone.text = "+62 896-5608-8434"
            binding.tvEmployeeId.text = "LP-ADM-2026-089"
            binding.tvLocation.text = "📍 Kota Bandung Central Hub"
            binding.tvShift.text = "⏰ Pagi (08:00 - 16:00)"
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar Aplikasi")
            .setMessage("Apakah Anda yakin ingin keluar dari akun Lega Paket?")
            .setPositiveButton("Ya, Keluar") { dialog, _ ->
                dialog.dismiss()
                prosesLogoutSesi()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun prosesLogoutSesi() {
        val sharedPref = activity?.getSharedPreferences("SesiLegaPaket", Context.MODE_PRIVATE)
        sharedPref?.edit()?.clear()?.apply()

        Toast.makeText(requireContext(), "Sesi berhasil dihapus.", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}