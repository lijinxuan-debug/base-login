package com.example.thirdstage

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.thirdstage.databinding.ActivityRegisterBinding
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置密码长度限制
        binding.inPass.filters = arrayOf(InputFilter.LengthFilter(12))
        binding.inPass2.filters = arrayOf(InputFilter.LengthFilter(12))

        // 进行极简监听
        binding.inUser.doOnTextChanged { _, _, _, _ -> validateRegister() }
        binding.inPass.doOnTextChanged { _, _, _, _ -> validateRegister() }
        binding.inPass2.doOnTextChanged { _, _, _, _ -> validateRegister() }

        // 返回页面跳转
        binding.toolbar.setOnClickListener { finish() }

        // 监听日期选择器
        initDatePicker()

        // 进行注册功能
        binding.btnRegister.setOnClickListener {
            var num = 0
            // 检查账号是否为空
            if (binding.inUser.text.toString().isEmpty()) {
                binding.tlUser.error = getString(R.string.username_is_empty)
                num++
            }
            // 检查第一个密码框是否为空
            if (binding.inPass.text.toString().isEmpty()) {
                binding.tlPass.error = getString(R.string.password_is_empty)
                num++
            }
            // 检查第二个密码框是否为空
            if (binding.inPass2.text.toString().isEmpty()) {
                binding.tlPass2.error = getString(R.string.password_is_empty)
                num++
            }
            // 密码是否一致或者格式是否正确
            if (binding.inPass.text.toString() != binding.inPass.text.toString()) {
                binding.tlPass.error = getString(R.string.hint_pass2)
                binding.tlPass2.error = getString(R.string.hint_pass2)
                num++
            }
            // 检查是否已经选择日期
            if (binding.etDate.text.toString().isEmpty()) {
                binding.dataPicker.error = getString(R.string.datepicker_is_empty)
                num++
            }
            if (num != 0) {
                return@setOnClickListener
            }

            // TODO 校验通过将进行注册用户

        }
    }

    private fun validateRegister() {
        val u1 = binding.inUser.text.toString()
        val p1 = binding.inPass.text.toString()
        val p2 = binding.inPass2.text.toString()

        // 检验用户框
        if (u1.isNotEmpty()) {
            binding.tlUser.error = null
            binding.tlPass2.isErrorEnabled = false
        }

        // 校验第一个框（长度）
        if (p1.length in 1..5) {
            binding.tlPass.error = getString(R.string.hint)
        } else {
            binding.tlPass.error = null
            binding.tlPass.isErrorEnabled = false
        }

        // 校验第二个框（匹配）
        if (p2.isNotEmpty()) {
            if (p1 == p2) {
                binding.tlPass2.error = null
                binding.tlPass2.isErrorEnabled = false
            } else {
                binding.tlPass2.error = getString(R.string.hint_pass2)
            }
        }

        binding.dataPicker.error = null
        binding.dataPicker.isErrorEnabled = false

    }

    // 使用原生 DatePickerDialog，不依赖任何第三方库
    private fun initDatePicker() {
        binding.etDate.setOnClickListener {
            val str = binding.etDate.text.toString()
            // 设置日期选择器
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            if (str.isNotEmpty()) {
                val array = str.split("-")
                year = array[0].toInt()
                month = array[1].toInt() - 1
                day = array[2].toInt()
            }

            DatePickerDialog(
                this,
                { _, selectYear, selectMonth, selectDayOfMonth ->
                    val selectedDate = "$selectYear-${selectMonth + 1}-$selectDayOfMonth"
                    binding.etDate.setText(selectedDate)
                },
                year,
                month,
                day
            ).show()
            // 消除错误提示
            binding.dataPicker.error = null
        }

        // 右侧图标也应当有效果才可以
        binding.dataPicker.setEndIconOnClickListener {
            binding.etDate.performClick() // 直接复用点击效果
        }
    }

}