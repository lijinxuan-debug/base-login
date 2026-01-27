package com.example.thirdstage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doOnTextChanged
import com.example.thirdstage.databinding.ActivityLoginBinding
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 检验登陆状态
        checkLogin()
        checkInputPassword()
    }

    /**
     * 密码输入检测
     */
    private fun checkInputPassword() {
        // 绑定转换语言的按钮
        binding.btnCn.setOnClickListener { changeLanguage("zh-CN") }
        binding.btnEn.setOnClickListener { changeLanguage("en") }
        binding.btnJa.setOnClickListener { changeLanguage("ja") }

        // 目前规定长度为12
        binding.inPass.filters = arrayOf(InputFilter.LengthFilter(12))

        binding.inPass.doOnTextChanged { _,_,_,_ ->
            // 修改
            binding.tlPass.error = null
        }

        binding.inUser.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.inPass.requestFocus()
                true
            } else {
                false // 失败的话就按照系统默认处理
            }
        }

        binding.inPass.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
                true // 收起键盘
            } else {
                false
            }
        }

        binding.btnLogin.setOnClickListener {
            val passStr = binding.inPass.text.toString()
            if (isFastClick()) {
                return@setOnClickListener
            } else if (passStr.isEmpty()) {
                binding.tlPass.error = getString(R.string.password_is_empty)
            }
            login()
        }

        binding.btnRegister.setOnClickListener { view ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 检测是否出现点击过快
     */
    private fun isFastClick() : Boolean {
        // 先获取当前系统时间
        val now = System.currentTimeMillis()
        // 如果点击间隔小500ms视为快速点击
        if (now - lastClickTime < 500) return true
        lastClickTime = now
        return false
    }

    /**
     * 校验登录状态
     */
    private fun checkLogin() {
        // 获取登录信息
        val preferences = this.getSharedPreferences("teacher", MODE_PRIVATE)

        val intent = Intent(this, MainActivity::class.java)

        // 判断登陆状态，如果存在则直接登陆跳转
        if (preferences.getBoolean("isLogin",false)) {
            startActivity(intent)
            finish()
        }
    }

    /**
     * 登录方法
     */
    private fun login() {
        val username = binding.inUser.text.toString()
        val password = binding.inPass.text.toString()
        // 获取登录信息
        val preferences = this.getSharedPreferences("teacher", MODE_PRIVATE)

        val jsonString = preferences.getString("all_teachers","[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val teacher = jsonArray.getJSONObject(i)
            if (teacher.getString("username") == username && teacher.getString("password") == password) {
                addLoginState(preferences,username)
                // 跳转到主页面
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        if (preferences.getBoolean("isLogin",false)) {
            Toast.makeText(this,"登陆成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"账号或密码错误", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 添加当前登录用户和登陆状态
     */
    fun addLoginState(preference: SharedPreferences,username: String) {
        preference.edit {
            putString("currentUser",username)
            putBoolean("isLogin",true)
        }
    }

    /**
     * 切换语言
     */
    private fun changeLanguage(language: String) {
        val locale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(locale)
    }
}