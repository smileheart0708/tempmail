package com.temp.mail.util

import android.content.Context
import com.temp.mail.R
import java.util.Random

object EmailGenerator {

    private const val ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789"
    private const val USERNAME_LENGTH = 8

    fun generateRandomEmail(context: Context): String {
        val domains = context.resources.getStringArray(R.array.email_domains)
        if (domains.isEmpty()) {
            return "error@example.com" // Fallback
        }

        val random = Random()
        val username = StringBuilder(USERNAME_LENGTH)
        repeat(USERNAME_LENGTH) {
            username.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        }

        val domain = domains[random.nextInt(domains.size)]

        return "$username@$domain"
    }
}