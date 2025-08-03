package com.temp.mail.data.model

data class EmailAddress(
    val id: String,
    val address: String,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)