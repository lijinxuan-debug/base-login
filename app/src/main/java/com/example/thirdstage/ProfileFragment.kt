package com.example.thirdstage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.thirdstage.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.btnLogout.setOnClickListener {
            // 1. 先存数据
            val preferences = requireContext().getSharedPreferences("teacher", MODE_PRIVATE)
            preferences.edit().putBoolean("isLogin", false).apply()

            // 2. 提示放在跳转前
            Toast.makeText(requireContext().applicationContext, "已退出登录", Toast.LENGTH_SHORT).show()

            // 3. 获取 activity 引用并安全跳转
            val currentActivity = activity
            if (currentActivity != null) {
                val intent = Intent(currentActivity, LoginActivity::class.java)
                // 这一行非常重要，它会帮你把 MainActivity 一并清理掉，其实甚至不需要手动 finish
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}