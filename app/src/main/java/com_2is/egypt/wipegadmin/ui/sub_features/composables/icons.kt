package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.UploadState
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.RecordStateDialog

@Composable
fun RowScope.UploadStateIcon(state: UploadState) {
    var showStateDialog by remember {
        mutableStateOf(false)
    }
    if (showStateDialog) {
        RecordStateDialog(dismiss = { showStateDialog = false }, state)
    }
    if (state != UploadState.Uploading) {
        IconButton(
            onClick = {
                showStateDialog = true
            }, modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                state.icon!!,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(30.dp),
                tint = state.color
            )
        }

    } else CircularProgressIndicator(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(4.dp)
            .size(30.dp)
    )
}
@Composable
 fun SwipeRecording(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painterResource(R.drawable.ic_swap), null)
    }
}
