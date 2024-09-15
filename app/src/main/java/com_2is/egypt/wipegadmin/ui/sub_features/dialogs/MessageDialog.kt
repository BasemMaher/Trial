package com_2is.egypt.wipegadmin.ui.sub_features.dialogs

import android.graphics.ColorSpace
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import com_2is.egypt.wipegadmin.R
import java.lang.Exception

@Composable
fun MessageDialog(
    modifier: Modifier = Modifier,
    dismiss: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onDismissRequest = {
            dismiss()
        }, buttons = {
            Column {
                content()
            }

        })
}

@Composable
fun ProgressDialog() {
    MessageDialog {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
    }
}

@Composable
fun ShowErrorDialog(error: Throwable, dismiss: () -> Unit) {

    MessageDialog(dismiss = dismiss) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp)
        ) {
            ErrorIcon()
        }
        val scroll = rememberScrollState()
        Text(
            error.message ?: stringResource(id = R.string.error_body),
            modifier = Modifier
                .padding(8.dp)
                .heightIn(max = 300.dp)
                .verticalScroll(scroll)
        )
        DialogOkButton(dismiss, Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun DialogOkButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        text = { Text(stringResource(R.string.ok)) },

        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp)

    )
}

@Composable
fun ShowSuccessDialog(message: String, dismiss: () -> Unit) {

    MessageDialog(dismiss = dismiss) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp)
        ) {
            SuccessIcon()
        }
        Text(
            message,
            modifier = Modifier.padding(8.dp)
        )
        ExtendedFloatingActionButton(
            shape = MaterialTheme.shapes.small,
            onClick = { dismiss() },
            text = { Text(stringResource(R.string.ok)) },

            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
private fun ErrorIcon(modifier: Modifier = Modifier) {
    Card(shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = modifier
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                null,
                tint = Color.Red.copy(alpha = .6f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(88.dp)
                    .padding(4.dp)
            )
            Text(
                stringResource(R.string.error),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center, color = MaterialTheme.colors.error,
            )
        }
    }
}

@Preview
@Composable
private fun SuccessIcon() {
    Card(shape = MaterialTheme.shapes.medium, contentColor = Color.White) {
        Column(modifier = Modifier.padding(4.dp)) {
            Icon(
                imageVector = Icons.Rounded.Done,
                null,
                tint = Color.Green.copy(alpha = .6f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(88.dp)
                    .padding(4.dp)
            )
            Text(
                stringResource(R.string.success),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),

                color = Color.Green.copy(green = .5f),
            )
        }
    }
}

@Composable
fun MaterialsUploadedErrorDialog(onDismiss: () -> Unit) {
    ShowErrorDialog(
        error = Exception("Materials uploaded, You need admin permission  to add  records"),
        dismiss = onDismiss
    )
}

@Composable
fun RecordsUploadedErrorDialog(onDismiss: () -> Unit) {
    ShowErrorDialog(
        error = Exception("records uploaded, You need admin permission  to add  records"),
        dismiss = onDismiss
    )
}
