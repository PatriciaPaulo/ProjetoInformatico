package com.example.splmobile.viewmodels

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.activities.ActivityID
import com.example.splmobile.dtos.files.FileResponse
import com.example.splmobile.isCodeOK
import com.example.splmobile.services.files.FileService
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.VfsFileFromData
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FileViewModel (
    private val fileService : FileService,
    log: Logger
) : ViewModel() {

    // Classes
    sealed class FileActivityUploadUIState {
        object Success: FileActivityUploadUIState()
        data class Error(val error : String) : FileActivityUploadUIState()
        object Loading : FileActivityUploadUIState()
        object Empty : FileActivityUploadUIState()
    }

    // Variables
    private val _fileUploadUIState = MutableStateFlow<FileActivityUploadUIState>(
        FileActivityUploadUIState.Empty
    )
    private val log = log.withTag("FileViewModel")

    // Upload Activity File
    fun uploadActivityFile(
        activityID: ActivityID,
        fileBytes : ByteArray,
        filename : String,
        token : String
    ) = viewModelScope.launch {
        _fileUploadUIState.value = FileActivityUploadUIState.Loading

        var fileUploadResponse : FileResponse = viewModelScope.async() {
            log.v { "POST File Upload" }

            val files : Map<String, ByteArray> = mapOf(filename to fileBytes)

            try {
                fileService.postActivityUpload(
                    activityID,
                    token,
                    files
                )
            } catch (e : Exception) {
                log.e(e) { "Error" }
            }
        }.await() as FileResponse

        if(isCodeOK(fileUploadResponse.message)) {
            _fileUploadUIState.value = FileActivityUploadUIState.Success
        } else {
            _fileUploadUIState.value = FileActivityUploadUIState.Error(fileUploadResponse.message)
        }
    }

}