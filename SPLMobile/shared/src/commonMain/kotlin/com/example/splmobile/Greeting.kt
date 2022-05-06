package com.example.splmobile

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}