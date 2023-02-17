package com.creations.bawender.paperdoc

import java.time.LocalDate
import java.util.UUID

data class Note(
    val key: String = UUID.randomUUID().leastSignificantBits.toString(),
    val text: String,
    val createdAt: LocalDate
)