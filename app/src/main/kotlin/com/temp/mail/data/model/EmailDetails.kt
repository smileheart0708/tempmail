package com.temp.mail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailDetails(
    val id: String,
    val from: String,
    val to: List<String>,
    val subject: String,
    val body: String,
    val date: String
)