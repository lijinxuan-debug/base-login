package com.example.thirdstage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.doOnTextChanged
import com.example.thirdstage.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        binding.inPass.doOnTextChanged { text,_,_,_ ->
            val len = text?.length ?: 0
            // 校验密码是否为空
            if (len == 0) {
                binding.tlPass.error = getString(R.string.password_is_empty)
            } else {
                binding.tlPass.error = null
            }
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
                TODO("登录处理")
            } else {
                false
            }
        }

        binding.btnLogin.setOnClickListener {
            if (isFastClick()) {
                return@setOnClickListener
            }
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

    private fun changeLanguage(language: String) {
        val locale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(locale)
    }
}