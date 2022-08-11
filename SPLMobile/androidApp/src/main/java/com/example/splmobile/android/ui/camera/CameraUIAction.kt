package com.example.splmobile.android.ui.camera

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
    object OnSaveClick : CameraUIAction()
    object OnDiscardClick : CameraUIAction()
}