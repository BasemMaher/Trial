package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import com_2is.egypt.wipegadmin.R

@Composable
fun PasswordField(
    modifier: Modifier = Modifier.navigationBarsWithImePadding(),
    isPasswordVisible: Boolean,
    password: String,
    onChange: (password: String) -> Unit,
    isEnabled: Boolean = true,
    isPasswordValid: Boolean,
    showOrHideAction: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            visualTransformation = if (!isPasswordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            label = { Text(stringResource(R.string.password)) },
            value = password,
            onValueChange = onChange,
            enabled = isEnabled,
            isError = !isPasswordValid,
            trailingIcon = showOrHideAction,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(), shape = MaterialTheme.shapes.medium
        )
        if (!isPasswordValid) InvalidPasswordText()

    }

}

@Composable
fun ShowOrHidePasswordIcon(
    passwordIsVisible: Boolean,
    onChange: () -> Unit
) {
    IconButton(
        onClick = onChange,
        modifier = Modifier
            .padding(4.dp)
    ) {
        Icon(
            painter = painterResource(
                id = if (passwordIsVisible)
                    R.drawable.ic_visibility_on else
                    R.drawable.ic_visibility_off,
            ),
            contentDescription = null // decorative element
        )
    }
}

@Composable
fun InvalidPasswordText() {
    Text(
        stringResource(R.string.invalid_password),
        style = MaterialTheme.typography.overline.copy(color = MaterialTheme.colors.error),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

