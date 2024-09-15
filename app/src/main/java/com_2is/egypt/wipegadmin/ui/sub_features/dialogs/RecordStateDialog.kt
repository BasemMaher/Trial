package com_2is.egypt.wipegadmin.ui.sub_features.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.UploadState
import java.lang.Exception

@Composable
fun RecordStateDialog(dismiss: () -> Unit, state: UploadState) {

    when (state) {
        is UploadState.ErrorWithUpload -> ShowErrorDialog(
            error = Exception(state.errorMessage),
            dismiss
        )
        UploadState.NotUploaded -> MessageDialog(dismiss=dismiss) {
            Row {
                Text(
                    stringResource(R.string.record_not_uploaded),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            DialogOkButton(dismiss, Modifier.align(Alignment.CenterHorizontally))


        }
        is UploadState.Uploaded -> ShowSuccessDialog(
            message = "Record Successfully Uploaded \n${state.message}",
            dismiss
        )
        UploadState.Uploading -> {
        }
    }
}