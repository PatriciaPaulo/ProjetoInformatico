package com.example.splmobile.android.ui.camera

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.splmobile.android.R
import okhttp3.internal.wait

@Composable
fun CameraView(
    onImageCaptured : (Uri, Boolean) -> Unit,
    onError : (ImageCaptureException) -> Unit
) {
    val context = LocalContext.current
    var lensFacing by remember { mutableStateOf( CameraSelector.LENS_FACING_BACK ) }
    val imageCapture : ImageCapture = remember { ImageCapture.Builder().build() }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri : Uri? ->
        if (uri != null) {
            // onImageCaptured true if image was from gallery
            onImageCaptured(uri, true)
        }

        println("URI: $uri")
    }

    CameraPreviewView(imageCapture, lensFacing) { cameraUIAction ->
        when(cameraUIAction) {
            is CameraUIAction.OnCameraClick -> {
                imageCapture.takePicture(context, lensFacing, onImageCaptured, onError)
            }

            is CameraUIAction.OnSwitchCameraClick -> {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
            }

            is CameraUIAction.OnGalleryViewClick -> {
                if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewView(
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    cameraUIAction: (CameraUIAction) -> Unit
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder()
        .build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(
        modifier = Modifier
            .fillMaxSize())
    {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) { }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraControls(cameraUIAction)
        }
    }
}

@Composable
private fun CameraControls(cameraUIAction: (CameraUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // TODO Change Icons
        CameraControl(
            Icons.Sharp.Add,
            R.string.icn_camera_view_switch_camera_content_description,
            modifier= Modifier.size(64.dp),
            onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
        )

        CameraControl(
            Icons.Default.Call,
            R.string.icn_camera_view_camera_shutter_content_description,
            modifier= Modifier
                .size(64.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
        )

        CameraControl(
            Icons.Sharp.Edit,
            R.string.icn_camera_view_view_gallery_content_description,
            modifier= Modifier.size(64.dp),
            onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
        )

    }
}

@Composable
fun CameraControl(
    imageVector: ImageVector,
    contentDescId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = stringResource(id = contentDescId),
            modifier = modifier,
            tint = Color.White
        )
    }
}
