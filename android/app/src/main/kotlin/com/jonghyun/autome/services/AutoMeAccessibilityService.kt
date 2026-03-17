package com.jonghyun.autome.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.jonghyun.autome.data.AppDatabase
import com.jonghyun.autome.data.MessageEntity
import com.jonghyun.autome.utils.PiiMasker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoMeAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "AutoMeAI_Accessibility"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // 발신(Sent) 입력 감지 (간단한 Fallback 로직, 추후 고도화 필요)
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val node = event.source ?: return
            val text = node.text?.toString()
            if (!text.isNullOrBlank()) {
                saveSentMessage(text)
            }
        }
    }

    private fun saveSentMessage(originalText: String) {
        val maskedText = PiiMasker.maskText(originalText)
        val entity = MessageEntity(
            roomId = "unknown_room_from_accessibility",
            sender = "Me",
            message = maskedText,
            timestamp = System.currentTimeMillis(),
            isSentByMe = true
        )
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(applicationContext).messageDao().insertMessage(entity)
            Log.d(TAG, "Saved sent masked message")
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
