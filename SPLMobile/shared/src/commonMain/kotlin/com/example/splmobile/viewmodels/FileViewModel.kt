package com.example.splmobile.viewmodels

class FileViewModel : ViewModel() {

    // Classes
    sealed class FileUploadUIState {
        object Success: FileUploadUIState()
        data class Error(val error : String) : FileUploadUIState()
        object Loading : FileUploadUIState()
        object Empty : FileUploadUIState()
    }

    // Upload File
    //fun uploadFile()
}