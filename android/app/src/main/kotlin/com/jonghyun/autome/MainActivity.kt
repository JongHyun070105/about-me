package com.jonghyun.autome

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.jonghyun.autome.data.AppDatabase

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.jonghyun.autome/native"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "openAccessibilitySettings" -> {
                    startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    result.success(null)
                }
                "openNotificationSettings" -> {
                    startActivity(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    result.success(null)
                }
                "checkServicesEnabled" -> {
                    // 접근성 등 서비스 활성 체크 (구현 가능 시 보완)
                    result.success(true) 
                }
                "getMessageCount" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(applicationContext)
                        val count = db.messageDao().getMessageCount()
                        launch(Dispatchers.Main) {
                            result.success(count)
                        }
                    }
                }
                else -> result.notImplemented()
            }
        }
    }
}
