package com_2is.egypt.wipegadmin.ui.features.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val CONTROLLER_PASSWORD = "administratorB"
private const val ADMIN_UPLOAD_PASSWORD = "admin"

data class LoginState(
    val password: String = "",
    val passwordIsValid: Boolean = true,
    val passwordIsVisible: Boolean = false,
    val login: LoginType = LoginType.NotLoggedIn
)

sealed class LoginEvent() {
    object HideOrShowPassword : LoginEvent()
    object Login : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()

}

enum class LoginType {
    Admin,
    Controller,
    NotLoggedIn
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state
    fun addEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.HideOrShowPassword -> _state.value =
                _state.value.copy(passwordIsVisible = !_state.value.passwordIsVisible)
            LoginEvent.Login -> {
                val password = state.value.password
                _state.value =
                    if (password == CONTROLLER_PASSWORD || password == ADMIN_UPLOAD_PASSWORD)
                        state.value.copy(
                            login = when (password) {
                                CONTROLLER_PASSWORD -> LoginType.Controller
                                ADMIN_UPLOAD_PASSWORD -> LoginType.Admin
                                else -> LoginType.NotLoggedIn
                            }
                        )
                    else
                        state.value.copy(passwordIsValid = false)
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = state.value.copy(
                    password = event.password,
                    passwordIsValid = event.password.isNotEmpty()
                )
            }
        }

    }
}