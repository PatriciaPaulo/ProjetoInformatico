package com.example.splmobile

enum class HttpRequestUrls(val url: String) {
   websocket_emulator("ws://10.0.2.2:5001"),
   api_emulator("http://10.0.2.2:5000/"),
   websocket_patricia_network("ws://192.168.1.88:5001"),
   api_patricia_network("http://192.168.1.88:5000"),
   api_geocode("https://geocode.xyz/")

}