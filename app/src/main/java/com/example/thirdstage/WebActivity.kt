package com.example.thirdstage

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.thirdstage.bridge.WebInterface

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web)

        val webView = findViewById<WebView>(R.id.my_webview)
        val progressBar = findViewById<ProgressBar>(R.id.web_progress_bar)

        //绑定桥梁
        webView.addJavascriptInterface(WebInterface(this),"AndroidBridge")

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            // 确保手机版适配
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    if (progressBar.isGone) {
                        progressBar.visibility = View.VISIBLE
                    }
                    // 使用动画平滑更新进度（可选）
                    progressBar.setProgress(newProgress, true)
                }
                super.onProgressChanged(view, newProgress)
            }
        }

        webView.webViewClient = WebViewClient()

        val url = intent.getStringExtra("TARGET_URL") ?: "file:///android_asset/index.html"
        webView.loadUrl(url)
    }
}