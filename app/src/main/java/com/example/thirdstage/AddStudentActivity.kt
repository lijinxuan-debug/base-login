package com.example.thirdstage

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdstage.dataModel.Student
import com.example.thirdstage.databinding.ActivityAddStudentBinding
import java.util.Calendar
import java.util.UUID

class AddStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStudentBinding

    // 准备班级列表数据
    private val classOptions =
        arrayOf("计算机科学一班", "软件工程二班", "人工智能三班", "大数据应用四班")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 返回页面跳转
        binding.toolbar.setOnClickListener { finish() }

        // 监听日期选择器
        initDatePicker()
        // 初始化班级监听选择器
        initClassPicker()

        // 最后的保存操作
        binding.btnSave.setOnClickListener {
            // 1. 获取姓名
            val name = binding.inUser.text.toString()
            // 2. 获取性别 (RadioButton)
            val gender = if (binding.man.isChecked) "男" else "女"
            // 3. 获取日期和班级
            val date = binding.etDate.text.toString()
            val className = binding.etClassName.text.toString()

            if (name.isEmpty() || className.isEmpty()) {
                android.widget.Toast.makeText(this, "信息不完整", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 生成学生编号
            val studentId = UUID.randomUUID().toString().substring(0,8)

            // 4. 打包并返回（假设 Student 是你的数据类）
            val student = Student(studentId, name, gender, date, className)
            val intent = android.content.Intent()
            intent.putExtra("new_student_json", student.toJsonObject().toString())

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initClassPicker() {
        // 1. 点击输入框弹出
        binding.etClassName.setOnClickListener {
            // 加载写的弹窗布局
            val view = layoutInflater.inflate(R.layout.dialog_picker, null)
            val picker = view.findViewById<android.widget.NumberPicker>(R.id.number_picker)

            // 2. 配置滚轮，以下两个均是索引
            picker.minValue = 0
            picker.maxValue = classOptions.size - 1
            picker.displayedValues = classOptions
            // 禁止内部子 View 获取焦点
            picker.descendantFocusability = android.widget.NumberPicker.FOCUS_BLOCK_DESCENDANTS
            // 循环滑动
            picker.wrapSelectorWheel = true

            // 3. 弹窗
            android.app.AlertDialog.Builder(this)
                .setTitle("选择班级")
                .setView(view)
                .setPositiveButton("确定") { _, _ ->
                    binding.etClassName.setText(classOptions[picker.value])
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // 4. 点击右侧图标也能弹出
        binding.className.setEndIconOnClickListener {
            binding.etClassName.performClick()
        }
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