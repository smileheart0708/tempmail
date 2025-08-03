package com.temp.mail.data.model

data class EmailBody(
    val text: String? = null,
    val html: String? = null
)

data class EmailDetail(
    val id: String,
    val subject: String,
    val from: String,
    val to: String,
    val date: String,
    val body: EmailBody
)