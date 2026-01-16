package com.example.thirdstage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.thirdstage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    // 利用 lazy 懒加载 Fragment，只有第一次切换到它时才会实例化
    private val homeFragment by lazy { HomeFragment() }
    private val notificationFragment by lazy { NotificationFragment() }
    private val profileFragment by lazy { ProfileFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    private fun initNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment)
                R.id.nav_notifications -> switchFragment(notificationFragment)
                R.id.nav_profile -> switchFragment(profileFragment)
                else -> false
            }
        }

        // 默认选中
        if (currentFragment == null) {
            switchFragment(homeFragment)
        }
    }

    /**
     * 专业切换逻辑：add & show & hide
     * 这种方式不会导致 Fragment 重新触发 onCreateView，保持页面数据状态
     */
    private fun switchFragment(targetFragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()

        if (!targetFragment.isAdded) {
            // 如果还没添加过，先隐藏当前的，再添加新的
            currentFragment?.let { transaction.hide(it) }
            transaction.add(R.id.main_container, targetFragment)
        } else {
            // 如果已经添加过，直接隐藏当前，显示目标
            currentFragment?.let { transaction.hide(it) }
            transaction.show(targetFragment)
        }

        currentFragment = targetFragment
        transaction.commit()
        return true
    }
}