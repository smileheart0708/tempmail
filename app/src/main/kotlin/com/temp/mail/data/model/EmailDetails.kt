package com.temp.mail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailBody(
    val text: String? = null,
    val html: String? = null
)

@Serializable
data class EmailDetails(
    val id: String,
    val from: String,
    val to: List<String>,
    val subject: String,
    val body: EmailBody,
    val date: String
)