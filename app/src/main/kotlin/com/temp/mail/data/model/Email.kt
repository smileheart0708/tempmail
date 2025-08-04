package com.temp.mail.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Email(
    @SerialName("mailbox")
    val mailbox: String,
    @SerialName("id")
    val id: String,
    @SerialName("from")
    val from: String,
    @SerialName("to")
    val to: List<String>,
    @SerialName("subject")
    val subject: String,
    @SerialName("date")
    val date: String,
    @SerialName("posix-millis")
    val posixMillis: Long,
    @SerialName("size")
    val size: Int,
    @SerialName("seen")
    val seen: Boolean
)