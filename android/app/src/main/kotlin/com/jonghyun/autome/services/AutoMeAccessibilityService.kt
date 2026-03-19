package com.jonghyun.autome.services

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.jonghyun.autome.data.AppDatabase
import com.jonghyun.autome.data.MessageEntity
import com.jonghyun.autome.utils.PiiMasker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoMeAccessibilityService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val TAG = "AutoMeCaptured"

    private var lastCapturedText: String = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val textList = event.text ?: emptyList<CharSequence>()
            val text = textList.joinToString("")
            
            if (text.isEmpty() && lastCapturedText.isNotEmpty()) {
                // 기존 텍스트가 있다가 비워졌다면 전송 버튼을 누른 것으로 간주
                Log.d(TAG, "Sent Message Detected (Cleared): $lastCapturedText")
                saveSentMessage(lastCapturedText)
                lastCapturedText = ""
            } else if (text.isNotEmpty()) {
                // 입력되는 과정을 계속 업데이트
                lastCapturedText = text
            }
        }
    }

    private fun saveSentMessage(text: String) {
        val maskedText = PiiMasker.maskText(text)
        scope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val message = MessageEntity(
                roomId = "accessibility_sent",
                sender = "나 (보냄)",
                message = maskedText,
                timestamp = System.currentTimeMillis(),
                isSentByMe = true
            )
            db.messageDao().insertMessage(message)
            Log.d(TAG, "Sent message saved to DB: $maskedText")
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
    }
}
