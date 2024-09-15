package com_2is.egypt.wipegadmin.ui.features.controller.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com_2is.egypt.wipegadmin.domain.gateways.PreferenceGateway
import com_2is.egypt.wipegadmin.domain.gateways.RecordsDao
import com_2is.egypt.wipegadmin.domain.gateways.UploadMaterialDao
import com_2is.egypt.wipegadmin.domain.repositories.ItemsRepository
import com_2is.egypt.wipegadmin.ui.core.urlIsValid
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: Lazy<ItemsRepository>,
    private val recordsDao: Lazy<RecordsDao>,
    private val materialDao: Lazy<UploadMaterialDao>,
    private val preferences: Lazy<PreferenceGateway>
) : ViewModel() {
    private val _state = MutableStateFlow(ControllerState())

    init {
        viewModelScope.launch {
            _state.run { value = value.copy(loading = true) }
            _state.value = repository.get().getSavedState().copy(loading = false)
        }

    }

    val state: StateFlow<ControllerState> = _state
    fun addEvent(event: ControllerEvent): Unit = when (event) {
        ControllerEvent.FetchItems -> fetchRecords()
        ControllerEvent.ShowOrHidePassword -> showOrHidePassword()
        is ControllerEvent.ChangePassword -> changePassword(event.password)
        is ControllerEvent.ChangeAuthURL -> changeAuthURL(event.authURL)
        is ControllerEvent.ChangeItemsURL -> changeItemsURL(event.itemsURL)
        is ControllerEvent.ChangeUserName -> changeUserName(event.userName)
        is ControllerEvent.ChangeGrantType -> changeGrantType(event.grantType)
        ControllerEvent.ResultHandled -> resultHandled()
        is ControllerEvent.ChangeUploadRecordURL -> changeUploadRecordURL(event.uploadRecordURL)
        ControllerEvent.FetchToken -> fetchToken()
        is ControllerEvent.ChangeSelectedSection -> changeSelectedSection(event.section)
        is ControllerEvent.ChangeSelectedScreen -> changeSelectedScreen(event.screen)
        ControllerEvent.DeleteRecords -> deleteRecords()
        is ControllerEvent.ChangeMaterialsURL -> changeMaterialsURL(event.materialsURL)
        is ControllerEvent.ChangeUploadMaterialURL -> changeUploadMaterialURL(event.uploadRecordURL)
        ControllerEvent.DeleteMaterials -> deleteMaterials()
        ControllerEvent.FetchMaterials -> fetchMaterials()
    }

    private fun deleteRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.run {
                value = value.copy(loading = true)
                val deletedCount = recordsDao.get().deleteAll()
                preferences.get().addRecordLock = false
                value = value.copy(
                    loading = false,
                    fetchResult = ServerResult.RecordsDeleted(deletedCount)
                )
            }
        }
    }

    private fun deleteMaterials() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.run {
                value = value.copy(loading = true)
                val deletedCount = materialDao.get().deleteAll()
                preferences.get().addMaterialLock = false
                value = value.copy(
                    loading = false,
                    fetchResult = ServerResult.RecordsDeleted(deletedCount)
                )
            }
        }
    }

    private fun changeSelectedSection(section: ControllerSection) {
        _state.run { value = value.copy(selectedSection = section) }
    }

    private fun changeSelectedScreen(screen: ControllerScreens) {
        _state.run { value = value.copy(selectedScreen = screen) }
    }

    private fun fetchToken() {
        val validatedState = state.value.validate(false)
        if (validatedState.isInputsValid) {
            viewModelScope.launch { repository.get().fetchToken(_state) }
        } else {
            _state.value = validatedState
        }
    }

    private fun resultHandled() = _state.run {
        value = value.copy(fetchResult = null)
    }


    private fun showOrHidePassword() = _state.run {
        value = value.run { copy(passwordIsVisible = !passwordIsVisible) }
    }

    private fun changePassword(password: String) = _state.run {
        value = value.copy(password = password, isPasswordValid = password.isNotEmpty())
    }

    private fun changeUserName(userName: String) = _state.run {
        value = value.copy(userName = userName, isUserNameValid = userName.isNotEmpty())
    }

    private fun changeGrantType(grantType: String) = _state.run {
        value = value.copy(grantType = grantType, isGrantTypeValid = grantType.isNotEmpty())
    }

    private fun changeAuthURL(authURL: String) = _state.run {
        value = value.copy(authURL = authURL, isAuthURLValid = urlIsValid(authURL))
    }

    private fun changeItemsURL(itemsURL: String) = _state.run {
        value = value.copy(itemsURL = itemsURL, isItemsURLValid = urlIsValid(itemsURL))
    }

    private fun changeUploadRecordURL(uploadRecordURL: String) = _state.run {
        value = value.copy(
            uploadRecordURL = uploadRecordURL,
            isUploadItemURLIsValid = urlIsValid(uploadRecordURL)
        )
    }

    private fun changeUploadMaterialURL(uploadMaterialUrl: String) = _state.run {
        value = value.copy(
            uploadMaterialURL = uploadMaterialUrl,
            isUploadMaterialURLIsValid = urlIsValid(uploadMaterialUrl)
        )
    }

    private fun changeMaterialsURL(materialsURL: String) = _state.run {
        value = value.copy(
            materialsURL = materialsURL,
            isMaterialsURLValid = urlIsValid(materialsURL)
        )
    }

    private fun fetchRecords() {
        val validatedState = state.value.validate()
        if (validatedState.isInputsValid) {
            viewModelScope.launch { repository.get().fetchRecords(_state) }
        } else {
            _state.value = validatedState
        }
    }

    private fun fetchMaterials() {
        val validatedState = state.value.validate()
        if (validatedState.isInputsValid) {
            viewModelScope.launch { repository.get().fetchMaterials(_state) }
        } else {
            _state.value = validatedState
        }
    }
}