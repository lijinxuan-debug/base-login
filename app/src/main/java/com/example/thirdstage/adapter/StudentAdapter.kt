package com.example.thirdstage.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.item_name)
        val tvClass: TextView = view.findViewById(R.id.item_class)
        val tvId: TextView = view.findViewById(R.id.item_student_id)
        val tvDay: TextView = view.findViewById(R.id.item_info)
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
            StudentViewHolder(view)
        } else {
            // 这里加载你刚创建的那个 item_footer.xml
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 4. 【新增】分类型绑定
        if (holder is StudentViewHolder) {
            val student = studentList[position]
            holder.tvName.text = student.name
            holder.tvClass.text = student.className
            val context = holder.itemView.context
            holder.tvId.text = context.getString(R.string.student_id, student.studentId)
            holder.tvDay.text = student.birthDate
            holder.tvId.visibility = if (isGrid) View.GONE else View.VISIBLE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, WebActivity::class.java)
                val encodedName = encode(student.name, "UTF-8")
                val webPath = "file:///android_asset/index.html?name=$encodedName"
                intent.putExtra("TARGET_URL", webPath)
                context.startActivity(intent)
            }
        } else if (holder is FooterViewHolder) {
            // 5. 【关键】处理加载动画的逻辑
            if (hasMore) {
                holder.progressBar.visibility = View.VISIBLE
                holder.tvNoMore.text = "正在努力加载中..."
            } else {
                holder.progressBar.visibility = View.GONE
                holder.tvNoMore.text = "没有更多数据了"
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