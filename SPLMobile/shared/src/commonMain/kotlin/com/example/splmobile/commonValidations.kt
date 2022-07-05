package com.example.splmobile

fun isEmailValid(email : String) : Boolean {
    val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

    return EMAIL_REGEX.toRegex().matches(email)
}

fun isTextFieldEmpty(text : String) : Boolean {
    return text.isEmpty()
}