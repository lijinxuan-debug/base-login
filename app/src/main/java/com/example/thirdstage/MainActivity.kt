package com.example.thirdstage

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.thirdstage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        binding.inPassword.filters = arrayOf(InputFilter.LengthFilter(12))

        binding.inPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) < 6) {
                    binding.inPassword.error = getString(R.string.hint)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s?.length ?: 0) >= 6) {
                    binding.inPassword.error = null
                }
            }

        })

        binding.inUser.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.inPassword.requestFocus()
                true
            } else {
                false // 失败的话就按照系统默认处理
            }
        }

        binding.inPassword.setOnEditorActionListener { v, actionId, event ->
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
            val textUser = binding.inPassword.text.toString()
            if (textUser.length < 6) {
                binding.inPassword.error = getString(R.string.hint)
                binding.inPassword.requestFocus()
            }
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