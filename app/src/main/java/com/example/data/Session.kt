package com.example.data

/**
 * Converted from src/lib/session.ts
 * This file keeps the logic compatible with plain Kotlin without Next.js cookies.
 */
data class SessionUser(
    val email: String,
    val name: String = "Jane Doe"
)

data class SessionPayload(
    val user: SessionUser
)

object SessionManager {
    private const val SESSION_SECRET = "fallback-secret-for-development"

    fun encrypt(payload: SessionPayload): String {
        return payload.user.email + ":" + payload.user.name + ":" + SESSION_SECRET
    }

    fun decrypt(input: String): SessionPayload? {
        if (input.isBlank()) return null
        val parts = input.split(":")
        if (parts.size < 3) return null
        return SessionPayload(
            user = SessionUser(
                email = parts[0],
                name = parts[1]
            )
        )
    }

    fun loginAndSetCookie(data: Map<String, Any?>): String {
        val user = SessionUser(
            email = data["email"]?.toString() ?: "",
            name = "Jane Doe"
        )
        return encrypt(SessionPayload(user))
    }

    fun logout(): String = ""

    fun getSession(sessionCookie: String?): SessionPayload? {
        if (sessionCookie.isNullOrBlank()) return null
        return decrypt(sessionCookie)
    }

    fun updateSession(sessionCookie: String?): String? {
        if (sessionCookie.isNullOrBlank()) return null
        return decrypt(sessionCookie)?.let { encrypt(it) }
    }
}
