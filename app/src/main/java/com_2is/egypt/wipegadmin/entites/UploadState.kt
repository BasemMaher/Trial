package com_2is.egypt.wipegadmin.entites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
sealed class UploadState {
    @Serializable
    object NotUploaded : UploadState()

    @Serializable
    data class ErrorWithUpload(
        val errorMessage: String
    ) : UploadState()

    val icon
        get() = when (this) {
            is NotUploaded -> Icons.Rounded.Info
            is Uploaded -> Icons.Rounded.Done
            is ErrorWithUpload -> Icons.Rounded.Warning
            else -> null
        }
    val color
        get() = when (this) {
            is NotUploaded -> Color.LightGray
            is Uploaded -> Color.Green
            is ErrorWithUpload -> Color.Red
            else -> Color.White
        }

    @Serializable
    data class Uploaded(val message: String) : UploadState()

    @Serializable
    object Uploading : UploadState()

}