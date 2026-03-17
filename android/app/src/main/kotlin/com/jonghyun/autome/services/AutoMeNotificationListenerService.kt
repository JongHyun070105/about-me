package com.jonghyun.autome.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.app.Notification
import com.jonghyun.autome.data.AppDatabase
import com.jonghyun.autome.data.MessageEntity
import com.jonghyun.autome.utils.PiiMasker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoMeNotificationListenerService : NotificationListenerService() {
    companion object {
        private const val TAG = "AutoMeAI_Notification"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val notification = it.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE) ?: "unknown_sender"
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            
            if (!text.isNullOrBlank()) {
                saveReceivedMessage(it.packageName, title, text)
            }
            
            // TODO: MessagingStyle을 통한 단방향이 아닌 다중 Historic Messages 파싱 고도화
        }
    }

    private fun saveReceivedMessage(roomId: String, sender: String, originalText: String) {
        val maskedText = PiiMasker.maskText(originalText)
        val entity = MessageEntity(
            roomId = roomId,
            sender = sender,
            message = maskedText,
            timestamp = System.currentTimeMillis(),
            isSentByMe = false
        )
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(applicationContext).messageDao().insertMessage(entity)
            Log.d(TAG, "Saved received message from \$sender")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
