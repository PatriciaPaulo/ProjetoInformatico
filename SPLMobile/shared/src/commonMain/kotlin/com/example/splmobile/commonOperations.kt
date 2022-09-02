package com.example.splmobile

fun isEmailValid(email : String) : Boolean {
    val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

    return EMAIL_REGEX.toRegex().matches(email)
}

fun isTextFieldEmpty(text : String) : Boolean {
    return text.isEmpty()
}

fun isCodeOK(message : String) : Boolean {
    return message.substring(0,3) == "200"
}

fun passwordsMatch(password : String, passwordConfirmation : String): Boolean {
    return password == passwordConfirmation
}

fun isNumber(text : String) : Boolean {
    val NUMBER_REGEX = "^[0-9]*\$"

    return NUMBER_REGEX.toRegex().matches(text)
}