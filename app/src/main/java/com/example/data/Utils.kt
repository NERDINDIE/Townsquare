package com.example.data

/**
 * Converted from src/lib/utils.ts
 */
fun cn(vararg inputs: String?): String {
    return inputs
        .filterNotNull()
        .filter { it.isNotBlank() }
        .joinToString(" ")
}
