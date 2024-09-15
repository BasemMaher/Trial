package com_2is.egypt.wipegadmin.ui.sub_features.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerEvent
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerViewModel

@Composable
fun DeleteAlertDialog(
    dismiss: () -> Unit,
    deleteMessage: String,
    onDeleteClicked: () -> Unit,
) = MessageDialog(dismiss = dismiss) {
    Box(Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.Warning, null, tint = Color.Yellow)
    }
    Text(text = deleteMessage)
    Row(Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { dismiss() },
            modifier = Modifier.weight(1f)
        ) { Text(text = stringResource(R.string.cancel)) }
        TextButton(
            onClick = {
                onDeleteClicked()
                dismiss()
            },
            Modifier
                .background(MaterialTheme.colors.error)
                .weight(1f)
        ) {
            Text(text = stringResource(R.string.delete), color = Color.White)
        }
    }
}
