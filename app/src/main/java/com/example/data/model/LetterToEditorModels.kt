package com.example.data.model

data class LetterToEditor(
    val id: String,
    val recipientId: String,
    val recipientName: String, // e.g., "Metro Editorial Board", "Tech Chronicle Editor"
    val recipientType: String, // "CHANNEL" or "SPACE"
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val messageBody: String,
    val categoryTag: String, // "Correction Request", "Op-Ed Submission", "Whistleblower Tip", "General Feedback", "Urgent Inquiry"
    val timestampFormatted: String,
    val status: String = "Sent to Desk", // "Sent to Desk", "Under Review", "Editor Replied", "Published in Journal"
    val editorReply: String? = null,
    val editorReplyTimestamp: String? = null,
    val isStarred: Boolean = false
)
