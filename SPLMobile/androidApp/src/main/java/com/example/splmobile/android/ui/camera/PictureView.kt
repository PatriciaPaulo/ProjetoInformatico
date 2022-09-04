package com.example.splmobile.android.ui.camera

import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.splmobile.android.viewmodel.CameraViewModel
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import com.example.splmobile.android.R
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.FileViewModel
import com.example.splmobile.objects.activities.ActivityID
import java.io.File

private val TAG = "PIC/VIEW"

@Composable
fun PictureView(
    authViewModel: AuthViewModel,
    fileViewModel: FileViewModel,
    cameraViewModel: CameraViewModel,
    onImageRejected : (Boolean) -> Unit
) {
    val uri = cameraViewModel.getUri()

    val fromGallery = cameraViewModel.getFromGallery()

    if (uri != null) {
        val path = uri.path

        val file = File(path)
        val fileBytes = file.readBytes()

        var filename_op = path!!.split("/")
        var filename = filename_op.last()

        PicturePreviewView(uri) { cameraUIAction ->
            when (cameraUIAction) {
                is CameraUIAction.OnSaveClick -> {
                    fileViewModel.uploadActivityFile(ActivityID(1), fileBytes, filename, authViewModel.tokenState.value)
                    Log.v(TAG, "Image saved on server")
                }

                is CameraUIAction.OnDiscardClick -> {
                    // Delete picture (from device) if it was taken with camera
                    if (!fromGallery) {
                        // Delete Picture
                        val uriPath = Path(uri.path!!)
                        uriPath.deleteIfExists()
                        Log.v(TAG, "Photo Deleted")
                    }

                    // Reopen Camera
                    onImageRejected(false)
                }

                else -> { }
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
            painter = rememberAsyncImagePainter(
                model = uri
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