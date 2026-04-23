package com.aakriti.notesnative.ui.util

import java.util.concurrent.TimeUnit

fun relativeDayLabel(updatedAt: Long, now: Long = System.currentTimeMillis()): String {
  val diff = now - updatedAt
  val days = TimeUnit.MILLISECONDS.toDays(diff)
  return when (days) {
    0L -> "Today"
    1L -> "Yesterday"
    else -> "${days} days ago"
  }
}

