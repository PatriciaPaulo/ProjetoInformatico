package com.example.splmobile.android.ui.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.viewmodel.CameraViewModel
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import java.io.File
import java.util.concurrent.Executors


@Composable
fun CameraScreen(
    navController: NavController,
    cameraViewModel: CameraViewModel
) {
    var isPictureTaken = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if(!isPictureTaken.value) {
        CameraView(
            onImageCaptured = { uri, fromGallery ->
                Log.d("IMG", "Image Uri Captured from CameraView")
                Log.d("IMG", uri.toString())

                cameraViewModel.setUri(uri)
                cameraViewModel.setFromGallery(fromGallery)

                isPictureTaken.value = true
            },
            onError = { imageCaptureException ->
                Log.d("ERROR IMG", "An error occurred while trying to take a picture")
                // TODO Warn user of error
            }
        )
    } else {
        PictureView(
            cameraViewModel,
            onImageRejected = {
                isPictureTaken.value = it
            }
        )
    }
}

