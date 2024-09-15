package com_2is.egypt.wipegadmin.ui.features.controller.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com_2is.egypt.wipegadmin.ui.core.urlIsValid

data class ControllerState(
    val loading: Boolean = false,
    val userName: String = "",
    val isUserNameValid: Boolean = true,
    val password: String = "",
    val passwordIsVisible: Boolean = false,
    val isPasswordValid: Boolean = true,
    val authURL: String = "",
    val isAuthURLValid: Boolean = true,
    val itemsURL: String = "",
    val isItemsURLValid: Boolean = true,
    val uploadRecordURL: String = "",
    val isUploadItemURLIsValid: Boolean = true,
    val materialsURL: String = "",
    val isMaterialsURLValid: Boolean = true,
    val uploadMaterialURL: String = "",
    val isUploadMaterialURLIsValid: Boolean = true,
    val grantType: String = "",
    val isGrantTypeValid: Boolean = true,
    val isLoggedIn: Boolean = false,
    val fetchResult: ServerResult? = null,
    val selectedSection: ControllerSection = ControllerSection.Auth,
    val selectedScreen: ControllerScreens = ControllerScreens.MetaData
) {
    val isInputsValid
        get() = isAuthURLValid &&
                isItemsURLValid &&
                isPasswordValid &&
                isGrantTypeValid &&
                isUserNameValid &&
                isUploadItemURLIsValid &&
                isMaterialsURLValid &&
                isUploadMaterialURLIsValid
    val isAuthValid
        get() = isUserNameValid &&
                isPasswordValid &&
                isGrantTypeValid


    fun validate(includeItemsAndUploadRecord: Boolean = true) = copy(
        isUserNameValid = userName.isNotEmpty(),
        isPasswordValid = password.isNotEmpty(),
        isAuthURLValid = urlIsValid(authURL),
        isItemsURLValid = !includeItemsAndUploadRecord || urlIsValid(itemsURL),
        isUploadItemURLIsValid = !includeItemsAndUploadRecord || urlIsValid(uploadRecordURL),
        isGrantTypeValid = grantType.isNotEmpty(),
        isMaterialsURLValid = !includeItemsAndUploadRecord || urlIsValid(materialsURL),
        isUploadMaterialURLIsValid = !includeItemsAndUploadRecord || urlIsValid(uploadMaterialURL)
    )
}

enum class ControllerSection(val label: String) {
    Auth("Auth Section"),
    Login("Login Section"),
    Items("Items Section"),
    Upload("Upload Section"),
    Delete("Delete Section"),
    Fetch("Fetch Data Section")
}

enum class ControllerScreens(val label: String, val Icon: ImageVector) {
    MetaData("Record Meta", Icons.Rounded.Info),
    Controller("Controller", Icons.Rounded.Settings),
    Records("Records", Icons.Rounded.List),
    Materials("Materials", Icons.Rounded.List),
}

sealed class ServerResult {
    class ItemsFetched(val itemsCount: Int) : ServerResult()
    class RecordsDeleted(val itemsCount: Int) : ServerResult()
    class TokenFetched(val expires: String) : ServerResult()
    class Error(val error: Throwable) : ServerResult()
}
