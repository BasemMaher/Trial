package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com_2is.egypt.wipegadmin.R

@Composable
fun StringField(
    value: String,
    onChange: (newValue: String) -> Unit,
    isValidInput: Boolean,
    title: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column {
        OutlinedTextField(
            label = { Text(title, style = TextStyle(fontSize = 14.sp)) },
            value = value,
            enabled = isEnabled,
            onValueChange = onChange,
            isError = !isValidInput, textStyle = TextStyle(fontSize = 14.sp),
            modifier = modifier
                .padding(padding)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon
        )
    }
    if (!isValidInput) InvalidInputText()

}

@Composable
fun InvalidInputText() {
    Text(
        stringResource(R.string.invalid),
        style = MaterialTheme.typography.overline.copy(
            color = MaterialTheme.colors.error,
            fontSize = 12.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

