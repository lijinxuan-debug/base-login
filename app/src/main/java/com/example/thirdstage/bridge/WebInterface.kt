package com.example.thirdstage.bridge

import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class WebInterface(private val mContext: Context) {
    private var mToast: Toast? = null

    private fun showSingleToast(message: String) {
        (mContext as? Activity)?.runOnUiThread {
            // 如果上一个 Toast 还在，立刻让它消失
            mToast?.cancel()

            // 创建并显示新的
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
            mToast?.show()
        }
    }

    @JavascriptInterface
    fun showToastHello(name: String) {
        showSingleToast("你好！我是 $name，很高兴认识你！")
    }

    @JavascriptInterface
    fun showToastWarn(){
        showSingleToast("请输入技能名称")
    }
}