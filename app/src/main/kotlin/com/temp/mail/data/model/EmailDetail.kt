package com.temp.mail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailDetail(
    val id: String,
    val subject: String,
    val from: String,
    val to: String,
    val date: String,
    val body: EmailBody
)