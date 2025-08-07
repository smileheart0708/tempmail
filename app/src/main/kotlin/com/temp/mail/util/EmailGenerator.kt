package com.temp.mail.util

import android.content.Context
import com.temp.mail.R
import kotlin.random.Random

object EmailGenerator {

    private val ALLOWED_CHARACTERS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private const val MIN_USERNAME_LENGTH = 6
    private const val MAX_USERNAME_LENGTH = 16

    fun generateRandomEmail(context: Context): String {
        val domains = context.resources.getStringArray(R.array.email_domains)
        if (domains.isEmpty()) {
            return "error@example.com" // Fallback
        }
        val usernameLength = Random.nextInt(MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH + 1)
        val username = (1..usernameLength)
            .map { ALLOWED_CHARACTERS.random() }
            .joinToString("")
        val domain = domains.random()

        return "$username@$domain"
    }
}
