package com.example.splmobile.android.ui.camera

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.splmobile.android.viewmodel.CameraViewModel
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import com.example.splmobile.android.R

@Composable
fun PictureView(
    cameraViewModel: CameraViewModel,
    onImageRejected : (Boolean) -> Unit
) {
    val uri = cameraViewModel.getUri()
    val fromGallery = cameraViewModel.getFromGallery()

    if (uri != null) {
        PicturePreviewView(uri) { cameraUIAction ->
            when (cameraUIAction) {
                is CameraUIAction.OnSaveClick -> {
                    println("URI: $uri")
                    //TODO Save image on server
                }

                is CameraUIAction.OnDiscardClick -> {
                    // Delete picture (from device) if it was taken with camera
                    if (!fromGallery) {
                        // Delete Picture
                        val uriPath = Path(uri.path!!)
                        uriPath.deleteIfExists()

                        // Reopen Camera
                        onImageRejected(false)
                    }
                }

            }
        }
    }
}

@Composable
private fun PicturePreviewView(
    uri : Uri,
    cameraUIAction: (CameraUIAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize())
    {
        Image(
            painter = rememberImagePainter(
                data = uri
            ),
            "Image" // TODO Change to res
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            PictureControls(cameraUIAction)
        }
    }
}

@Composable
private fun PictureControls(cameraUIAction: (CameraUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Discard Picture
        CameraControl(
            Icons.Sharp.Add,
            R.string.icn_camera_view_camera_shutter_content_description,
            modifier= Modifier.size(64.dp),
            onClick = { cameraUIAction(CameraUIAction.OnDiscardClick) }
        )

        Spacer(
            modifier = Modifier
                .size(64.dp)
        )

        // Save Picture
        CameraControl(
            Icons.Sharp.Edit,
            R.string.icn_camera_view_view_gallery_content_description,
            modifier= Modifier.size(64.dp),
            onClick = { cameraUIAction(CameraUIAction.OnSaveClick) }
        )

    }
}