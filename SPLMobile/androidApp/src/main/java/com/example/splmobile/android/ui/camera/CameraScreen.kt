package com.example.splmobile.android.ui.camera

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.splmobile.android.viewmodel.CameraViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.FileViewModel


@Composable
fun CameraScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    fileViewModel: FileViewModel,
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
            authViewModel,
            fileViewModel,
            cameraViewModel,
            onImageRejected = {
                isPictureTaken.value = it
            }
        )
    }
}

