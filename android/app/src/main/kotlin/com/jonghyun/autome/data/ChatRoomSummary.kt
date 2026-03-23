package com.jonghyun.autome.data

/**
 * Room DB 쿼리 결과를 담는 데이터 클래스: 채팅방 요약 정보
 */
data class ChatRoomSummary(
    val roomId: String,
    val lastSender: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val messageCount: Int
)
