package com.example.splmobile.android.viewmodel

import android.net.Uri
import com.example.splmobile.models.ViewModel

class CameraViewModel : ViewModel() {
    private var uri : Uri? = null
    private var fromGallery : Boolean = false

    fun getUri() = uri

    fun setUri(callerUri : Uri) {
        uri = callerUri
    }

    fun getFromGallery() = fromGallery

    fun setFromGallery(callerResult : Boolean) {
        fromGallery = callerResult
    }
}