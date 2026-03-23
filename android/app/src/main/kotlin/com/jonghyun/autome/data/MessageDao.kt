package com.jonghyun.autome.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Insert
    fun insertMessage(message: MessageEntity)

    @Insert
    fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(roomId: String, limit: Int = 20): List<MessageEntity>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    fun getAllRecentMessages(limit: Int = 50): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages")
    fun getMessageCount(): Int

    // ── 채팅방 목록 조회 ──
    @Query("""
        SELECT roomId, 
               sender AS lastSender,
               message AS lastMessage, 
               MAX(timestamp) AS lastTimestamp,
               COUNT(*) AS messageCount
        FROM messages
        GROUP BY roomId
        ORDER BY lastTimestamp DESC
    """)
    fun getDistinctRooms(): List<ChatRoomSummary>

    // ── 특정 채팅방 메시지 전체 조회 (오래된 순) ──
    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): List<MessageEntity>

    // ── 특정 채팅방 메시지 수 조회 ──
    @Query("SELECT COUNT(*) FROM messages WHERE roomId = :roomId")
    fun getMessageCountForRoom(roomId: String): Int
}
