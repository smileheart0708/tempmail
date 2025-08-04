package com.temp.mail.util

import android.content.Context
import com.temp.mail.R
import java.util.Random

object EmailGenerator {

    private const val ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val MIN_USERNAME_LENGTH = 6
    private const val MAX_USERNAME_LENGTH = 16

    fun generateRandomEmail(context: Context): String {
        val domains = context.resources.getStringArray(R.array.email_domains)
        if (domains.isEmpty()) {
            return "error@example.com" // Fallback
        }

        val random = Random()
        val usernameLength = random.nextInt(MAX_USERNAME_LENGTH - MIN_USERNAME_LENGTH + 1) + MIN_USERNAME_LENGTH
        val username = StringBuilder(usernameLength)
        repeat(usernameLength) {
            username.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        }

        val domain = domains[random.nextInt(domains.size)]

        return "$username@$domain"
    }
}