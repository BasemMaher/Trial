package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun URLField(
    url: String,
    onChange: (newURL: String) -> Unit,
    isURLValid: Boolean,
    isEnabled: Boolean = true,
    title: String
) {
    Column {
        OutlinedTextField(
            label = { Text(title) },
            value = url,
            onValueChange = {

                onChange(it)
            },
            isError = !isURLValid,
            enabled = isEnabled,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
        if (!isURLValid) InvalidURLText()

    }

}


@Composable
fun InvalidURLText() {
    Text(
        "URL is not valid",
        style = MaterialTheme.typography.overline.copy(color = MaterialTheme.colors.error),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
