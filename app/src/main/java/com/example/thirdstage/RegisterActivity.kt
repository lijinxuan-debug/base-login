package com.example.thirdstage

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.thirdstage.databinding.ActivityRegisterBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import androidx.core.content.edit

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
            val user = binding.inUser.text.toString()
            val pass1 = binding.inPass.text.toString()
            val pass2 = binding.inPass2.text.toString()
            // 检查账号是否为空
            if (user.isEmpty()) {
                binding.tlUser.error = getString(R.string.username_is_empty)
                num++
            }
            // 检查第一个密码框是否为空
            if (pass1.isEmpty()) {
                binding.tlPass.error = getString(R.string.password_is_empty)
                num++
            }
            // 检查第二个密码框是否为空
            if (pass2.isEmpty()) {
                binding.tlPass2.error = getString(R.string.password_is_empty)
                num++
            }
            // 密码是否一致或者格式是否正确
            if (pass1 != pass2) {
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

            // 校验通过将进行注册用户
            val register = registerUser(this, user, pass1, pass2, binding.etDate.text.toString())

            if (register) {
                // TODO 注册成功直接跳转到主页面
            }
        }
    }

    private fun registerUser(context: Context, username: String, pass1: String, pass2: String, date: String) : Boolean {
        val gender = when (binding.gender.checkedRadioButtonId) {
            R.id.man -> "男"
            R.id.female -> "女"
            else -> "男" // when语句必须要有默认值
        }

        // 获取指定的共享偏好文件
        val preferences = context.getSharedPreferences("teacher", MODE_PRIVATE)
        // 获取文件内部老师
        val oldJsonString = preferences.getString("all_teachers","[]") ?: "[]"
        // 转换成json数组
        val jsonArray = JSONArray(oldJsonString)

        // 从索引0开始遍历，不会越界
        for (i in 0 until jsonArray.length()) {
            val existUser = jsonArray.getJSONObject(i)
            if (existUser.getString("username") == username) {
                Toast.makeText(context,"该用户已存在",Toast.LENGTH_SHORT).show()
                return false
            }
        }

        val user = JSONObject().apply() {
            put("username", username)
            put("password", pass2)
            put("gender", gender)
            put("birthday", date)
        }

        jsonArray.put(user)

        // 将增加的新老师放到本地缓存
        preferences.edit { putString("all_teachers", jsonArray.toString()) }

        LoginActivity().addLoginState(preferences,username)
        return true

    }

    /**
     * 注册检验
     */
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

    /**
     * 使用原生 DatePickerDialog，不依赖任何第三方库
      */
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