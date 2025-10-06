package com.example.projectfoodmanager.util

import org.json.JSONObject

fun parseErrorMessage(errorBody: String?): Pair<String, String> {
    return try {
        val jsonObject = JSONObject(errorBody ?: "")
        val errors = jsonObject.getJSONObject("errors")

        // Check if "auth"
        if (errors.has("auth")) {
            val authErrors = errors.getJSONArray("auth")
            val message = authErrors.optString(0, "Unknown error")
            Pair("auth", message)
        } else {
            Pair("general", "An unknown error occurred")
        }
    } catch (e: Exception) {
        Pair("exception", "An error occurred while parsing")
    }
}