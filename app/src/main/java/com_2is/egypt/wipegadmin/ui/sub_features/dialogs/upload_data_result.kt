package com_2is.egypt.wipegadmin.ui.sub_features.dialogs

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.work.WorkInfo
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.services.UploadWorker
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

@Composable
fun HandleUploadWorkInfo(
    workInfo: Flow<WorkInfo>,
    isAdmin: Boolean = false,
    onDismiss: (success: Boolean) -> Unit
) {
    workInfo.collectAsState(initial = null).value?.let { workState ->
        when (workState.state) {
            WorkInfo.State.RUNNING -> MessageDialog {
                CircularProgressIndicator()
            }
            WorkInfo.State.SUCCEEDED -> {
                val uploadedCount =
                    workState.outputData.getInt(UploadWorker.UPLOADED_COUNT_KEY, 0)
                ShowSuccessDialog(
                    message = "$uploadedCount Records Uploaded Successfully" + if (!isAdmin)
                        "\n You need admin permission  to add  records"
                    else ""
                ) {
                    onDismiss(true)
                }
            }
            WorkInfo.State.FAILED -> {
                val errorMessage =
                    if (isAdmin) workState.outputData.getString(UploadWorker.ERROR_DATA_KEY)
                        ?: ""
                    else stringResource(R.string.user_error_message)
                ShowErrorDialog(error = Exception(errorMessage)) {
                    onDismiss(false)
                }
            }
        }


    }
}

