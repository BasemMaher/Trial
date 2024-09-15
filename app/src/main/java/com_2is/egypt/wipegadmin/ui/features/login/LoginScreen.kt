package com_2is.egypt.wipegadmin.ui.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.navigateToAdminUpload
import com_2is.egypt.wipegadmin.ui.features.navigateToController
import com_2is.egypt.wipegadmin.ui.sub_features.composables.PasswordField
import com_2is.egypt.wipegadmin.ui.sub_features.composables.ShowOrHidePasswordIcon

@Composable
fun LoginScreen(navController: NavHostController) {
    val viewModel: LoginViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    if (state.login == LoginType.Admin)
        navController.navigateToAdminUpload()
    else if (state.login == LoginType.Controller)
        navController.navigateToController()

    com.google.accompanist.insets.ui.Scaffold(
        floatingActionButton = { LoginFAB(viewModel) },
        floatingActionButtonPosition = FabPosition.Center, bottomBar = {
            // We add a spacer as a bottom bar, which is the same height as
            // the navigation bar
            Spacer(
                Modifier
                    .navigationBarsHeight()
                    .fillMaxWidth()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .align(Alignment.Center),
            ) {
                LoginPasswordField(state, viewModel)

            }
        }
    }
}

@Composable
private fun LoginPasswordField(
    state: LoginState,
    viewModel: LoginViewModel
) {
    PasswordField(
        password = state.password,
        onChange = { newPassword ->
            viewModel.addEvent(
                LoginEvent.PasswordChanged(
                    newPassword
                )
            )
        },
        isPasswordVisible = state.passwordIsVisible,
        isPasswordValid = state.passwordIsValid
    ) {
        ShowOrHidePasswordIcon(state.passwordIsVisible) {
            viewModel.addEvent(LoginEvent.HideOrShowPassword)

        }
    }
}


@Composable
private fun LoginFAB(viewModel: LoginViewModel) {
    FloatingActionButton(
        onClick = {
            viewModel.addEvent(LoginEvent.Login)
        }, modifier = Modifier
            .padding(vertical = 16.dp)
            .imePadding()
            .fillMaxWidth(.8f)
    ) {
        Text(text = stringResource(R.string.login))
    }
}



