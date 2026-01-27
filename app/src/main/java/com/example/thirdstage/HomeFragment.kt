package com.example.thirdstage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thirdstage.adapter.StudentAdapter
import com.example.thirdstage.dataModel.Student
import com.example.thirdstage.databinding.FragmentHomeBinding
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class HomeFragment : Fragment(R.layout.fragment_home) {

    // 1. 定义一个私有的 binding 变量
    private var _binding: FragmentHomeBinding? = null
    // 这个属性只在 onCreateView 和 onDestroyView 之间有效
    private val binding get() = _binding!!

    private val studentList = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    private var isLoading = false // 是否正在请求网络
//    private var hasMore = true    // 是否还有更多数据（由后端返回或根据分页判断）
    private var currentPage = 1   // 当前页码

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.toolbar.inflateMenu(R.menu.list_menu)

        // 第一次只拿 10 条作为首页
        val firstPage = loadStudentsFromSP(0, 10)
        studentList.clear()
        studentList.addAll(firstPage)
        adapter = StudentAdapter(studentList)

        // 如果第一页就没装满，说明总数都没到 10 条，直接关掉加载更多
        if (firstPage.size < 10) adapter.hasMore = false

        // 把这个 adapter 真正挂载到 RecyclerView 上
        binding.studentList.layoutManager = LinearLayoutManager(requireContext())
        binding.studentList.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.liner_manager -> {
                    adapter.isGrid = false
                    binding.studentList.layoutManager = LinearLayoutManager(requireContext())
                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.grid_manager -> {
                    adapter.isGrid = true
                    val spanCount = 2 // 定义列数
                    val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)

                    // 使用你封装在 Adapter 里的逻辑
                    gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return adapter.getRecommendedSpanSize(position, spanCount)
                        }
                    }

                    binding.studentList.layoutManager = gridLayoutManager
                    adapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        binding.floatingButton.setOnClickListener {
            val intent = Intent(requireContext(), AddStudentActivity::class.java)
            startActivityForResult(intent,100)
        }

        // 绑定下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            // 执行你之前写的 DiffUtil 更新逻辑
            refreshLocalData()
        }

        // 绑定上拉加载
        binding.studentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && !isLoading && adapter.hasMore) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItem >= totalItemCount - 2) {
                        loadMoreData()
                    }
                }
            }
        })
    }

    private fun loadMoreData() {
        isLoading = true

        // 模拟 1 秒网络延迟（或者 IO 延迟）
        binding.studentList.postDelayed({
            // 从当前列表的长度开始取，取 10 条
            val nextData = loadStudentsFromSP(studentList.size, 10)
            val startPosition = studentList.size

            if (nextData.isEmpty()) {
                adapter.hasMore = false
            } else {
                studentList.addAll(nextData)
                if (nextData.size < 10) {
                    adapter.hasMore = false
                }
            }
            adapter.notifyItemRangeInserted(startPosition, nextData.size)

            isLoading = false
        }, 1000)
    }

    /**
     * 下拉刷新
     */
    private fun refreshLocalData() {
        binding.swipeRefreshLayout.postDelayed({
            val newData = loadStudentsFromSP(0,10)
            // 这里调用DiffUtil其实没有意义，展示
            adapter.updateData(newData) // 这里内部调用 DiffUtil

            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    private fun loadStudentsFromSP(offset: Int, limit: Int): List<Student> {
        val sp = requireContext().getSharedPreferences("student_prefs", android.content.Context.MODE_PRIVATE)
        val jsonStr = sp.getString("all_students", "[]") ?: "[]"
        val allList = mutableListOf<Student>()

        try {
            val jsonArray = JSONArray(jsonStr)
            // 1. 先算出这次取数的结束位置（不能超过总数）
            val end = if (offset + limit > jsonArray.length()) jsonArray.length() else offset + limit

            // 2. 只有当起始位置小于总数时，才去循环
            for (i in offset until end) {
                val studentJson = jsonArray.getJSONObject(i)
                allList.add(Student.fromJson(studentJson))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return allList
    }

    // 这里的 100 必须对应你跳转时填写的那个数字
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == android.app.Activity.RESULT_OK) {

            val jsonStr = data?.getStringExtra("new_student_json")

            if (jsonStr != null) {
                try {
                    // 解析同学
                    val newStudent = Student.fromJson(org.json.JSONObject(jsonStr))

                    val newList = mutableListOf<Student>().apply {
                        add(newStudent)       // 先放新同学（如果你想放第一行）
                        addAll(studentList)   // 再把原来的所有同学接在后面
                    }

                    // 3. 调用你刚才写的那个专业方法
                    // 它内部会自动跑 DiffUtil 计算，并执行 notifyItemInserted(0) 等动画
                    adapter.updateData(newList)

                    // 4. 更新 Fragment 里的本地引用，保持数据同步
                    studentList.clear()
                    studentList.addAll(newList)

                    // 5. 让列表滚回顶部（这样用户能立刻看到刚添加的人）
                    binding.studentList.scrollToPosition(0)

                    // 6. 存入 SP 永久保存
                    saveToLocal()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun saveToLocal() {
        val sp = requireContext().getSharedPreferences("student_prefs", android.content.Context.MODE_PRIVATE)
        sp.edit {

            // 把整个列表转成 JSON 字符串存起来
            val jsonArray = JSONArray()
            for (s in studentList) {
                jsonArray.put(s.toJsonObject())
            }
            putString("all_students", jsonArray.toString())
        }
    }

    // 4. 必须销毁，防止内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // 移除不需要的任务
        binding.studentList.removeCallbacks(null)
    }
}