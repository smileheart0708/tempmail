package com.temp.mail.data.model

data class Email(
    val id: String,
    val subject: String,
    val from: String,
    val date: String,
    val posixMillis: Long = 0L,
    val isRead: Boolean = false
)