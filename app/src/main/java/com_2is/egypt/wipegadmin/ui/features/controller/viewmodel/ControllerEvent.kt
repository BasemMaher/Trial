package com_2is.egypt.wipegadmin.ui.features.controller.viewmodel

sealed class ControllerEvent {
    object FetchItems : ControllerEvent()
    object FetchMaterials : ControllerEvent()
    object ShowOrHidePassword : ControllerEvent()
    object ResultHandled : ControllerEvent()
    object FetchToken : ControllerEvent()
    object DeleteRecords : ControllerEvent()
    object DeleteMaterials : ControllerEvent()
    data class ChangeSelectedSection(val section: ControllerSection) : ControllerEvent()
    data class ChangeSelectedScreen(val screen: ControllerScreens) : ControllerEvent()
    data class ChangePassword(val password: String) : ControllerEvent()
    data class ChangeUserName(val userName: String) : ControllerEvent()
    data class ChangeGrantType(val grantType: String) : ControllerEvent()
    data class ChangeAuthURL(val authURL: String) : ControllerEvent()
    data class ChangeItemsURL(val itemsURL: String) : ControllerEvent()
    data class ChangeMaterialsURL(val materialsURL: String) : ControllerEvent()
    data class ChangeUploadRecordURL(val uploadRecordURL: String) : ControllerEvent()
    data class ChangeUploadMaterialURL(val uploadRecordURL: String) : ControllerEvent()

}
