package com.example.data

/**
 * Converted from src/lib/sms-data.ts
 */
data class SmsProvider(
    val id: Int,
    val name: String
)

data class SmsMessage(
    val from: String,
    val content: String
)

data class SmsThread(
    val id: Int,
    val provider: SmsProvider,
    val timestamp: String,
    val messages: List<SmsMessage>,
    val isSpam: Boolean? = null,
    val name: String,
    val avatar: String,
    val fallback: String,
    val unread: Int,
    val type: String,
    val status: String
)

val providers = listOf(
    SmsProvider(101, "Weather Alerts"),
    SmsProvider(102, "NewsFlash"),
    SmsProvider(103, "Townsquare Bank"),
    SmsProvider(104, "Local Deals"),
    SmsProvider(201, "Unknown Sender")
)

val smsThreads = listOf(
    SmsThread(
        id = 6,
        provider = providers[0],
        name = "Olivia",
        avatar = "https://github.com/randomuser-olivia.png",
        fallback = "O",
        timestamp = "9:15 AM",
        messages = listOf(
            SmsMessage("provider", "Hey! Saw we matched on Rendezvous. I loved your profile picture, is that from your trip to Italy?")
        ),
        isSpam = false,
        unread = 1,
        type = "match",
        status = "online"
    ),
    SmsThread(
        id = 1,
        provider = providers[0],
        name = "Emily White",
        avatar = "https://github.com/randomuser2.png",
        fallback = "EW",
        timestamp = "2:45 PM",
        messages = listOf(
            SmsMessage("provider", "Hey, did you see the latest news about the downtown market?"),
            SmsMessage("me", "No, what happened?")
        ),
        isSpam = false,
        unread = 2,
        type = "human",
        status = "online"
    ),
    SmsThread(
        id = 2,
        provider = providers[2],
        name = "Townsquare Bank",
        avatar = "",
        fallback = "TB",
        timestamp = "1:10 PM",
        messages = listOf(
            SmsMessage("provider", "Townsquare Bank Alert: A charge of $1,250 for \"Electronics\" was just approved. If this was not you, please call us immediately at (555)-0123.")
        ),
        isSpam = true,
        unread = 1,
        type = "ai",
        status = "online"
    ),
    SmsThread(
        id = 3,
        provider = providers[1],
        name = "Local News Bot",
        avatar = "",
        fallback = "NB",
        timestamp = "11:30 AM",
        messages = listOf(
            SmsMessage("provider", "Here are today's top headlines for you.")
        ),
        isSpam = false,
        unread = 0,
        type = "ai",
        status = "online"
    ),
    SmsThread(
        id = 4,
        provider = providers[3],
        name = "John Smith",
        avatar = "https://github.com/randomuser1.png",
        fallback = "JS",
        timestamp = "Yesterday",
        messages = listOf(
            SmsMessage("provider", "Can you send over the draft?")
        ),
        isSpam = false,
        unread = 0,
        type = "human",
        status = "offline"
    ),
    SmsThread(
        id = 5,
        provider = providers[4],
        name = "URGENT: Your Package",
        avatar = "",
        fallback = "!",
        timestamp = "Yesterday",
        messages = listOf(
            SmsMessage("provider", "NOTICE: Your package with tracking ID 81274-A is being held due to an incomplete address. Please update your details here to avoid return: bit.ly/fakelink")
        ),
        isSpam = true,
        unread = 1,
        type = "ai",
        status = "online"
    )
)
