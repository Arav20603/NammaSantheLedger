package com.namma.santhe.ledger.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatAmount(amount: Double): String {
    return if (amount == kotlin.math.floor(amount)) {
        amount.toInt().toString()
    } else {
        String.format("%.2f", amount)
    }
}

fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 172_800_000 -> "Yesterday"
        else -> SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}

fun String.initials(): String {
    val parts = this.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        parts.isNotEmpty() && parts[0].isNotBlank() -> parts[0].take(2).uppercase()
        else -> "??"
    }
}
