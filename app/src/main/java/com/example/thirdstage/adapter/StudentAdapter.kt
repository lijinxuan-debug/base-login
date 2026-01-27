package com.example.thirdstage.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdstage.R
import com.example.thirdstage.WebActivity
import com.example.thirdstage.dataModel.Student
import com.example.thirdstage.diffUtil.StudentDiffCallback
import java.net.URLEncoder.encode

class StudentAdapter(private var studentList: List<Student>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 【新增】定义两种布局标识
    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    var isGrid: Boolean = false
    var hasMore: Boolean = true // 【新增】控制是否显示加载动画

    // 1. 学生卡片 ViewHolder (保持不变)
    // 在 StudentViewHolder 内部增加一个绑定方法
    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.item_name)
        val tvClass: TextView = view.findViewById(R.id.item_class)
        val tvId: TextView = view.findViewById(R.id.item_student_id)
        val tvDay: TextView = view.findViewById(R.id.item_info)

        fun bind(student: Student, isGrid: Boolean) {
            tvName.text = student.name
            tvClass.text = student.className
            tvId.text = itemView.context.getString(R.string.student_id, student.studentId)
            tvDay.text = student.birthDate
            tvId.visibility = if (isGrid) View.GONE else View.VISIBLE
        }
    }

    // 2. 【新增】加载进度条 ViewHolder
    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val tvNoMore: TextView = view.findViewById(R.id.tv_no_more)
    }

    // 3. 【新增】根据位置判断该显示哪种布局
    override fun getItemViewType(position: Int): Int {
        // 如果是最后一位，显示 Footer
        return if (position == studentList.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
            val holder = StudentViewHolder(view)

            // 【优化】点击监听只在创建时设置一次！
            holder.itemView.setOnClickListener {
                val position = holder.bindingAdapterPosition // 获取最新位置
                if (position != RecyclerView.NO_POSITION) {
                    val student = studentList[position]
                    val context = it.context
                    val intent = Intent(context, WebActivity::class.java)
                    // 这里的 encode 可以考虑放在 Student 类里作为成员变量，点击直接取
                    val encodedName = encode(student.name, "UTF-8")
                    intent.putExtra("TARGET_URL", "file:///android_asset/index.html?name=$encodedName")
                    context.startActivity(intent)
                }
            }
            holder
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StudentViewHolder -> holder.bind(studentList[position], isGrid)
            is FooterViewHolder -> {
                holder.progressBar.visibility = if (hasMore) View.VISIBLE else View.GONE
                holder.tvNoMore.text = if (hasMore) "正在努力加载中..." else "没有更多数据了"
            }
        }
    }

    // 6. 【修改】总数 = 学生数 + 1 个 Footer
    override fun getItemCount(): Int = studentList.size + 1

    fun updateData(newList: List<Student>) {
        val diffCallback = StudentDiffCallback(this.studentList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.studentList = newList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
}