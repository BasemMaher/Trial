package com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel

import androidx.compose.ui.res.colorResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import com_2is.egypt.wipegadmin.domain.gateways.PreferenceGateway
import com_2is.egypt.wipegadmin.domain.gateways.ServerMaterialDao
import com_2is.egypt.wipegadmin.domain.gateways.UploadMaterialDao
import com_2is.egypt.wipegadmin.entites.MaterialInputs
import com_2is.egypt.wipegadmin.entites.ServerMaterial
import com_2is.egypt.wipegadmin.entites.createUploadMaterial
import com_2is.egypt.wipegadmin.ui.core.pager
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMaterialViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferenceGateway: Lazy<PreferenceGateway>,
    private val serverMaterialDao: Lazy<ServerMaterialDao>,
    private val materialsDao: Lazy<UploadMaterialDao>,
) : ViewModel() {
    private val _state =
        MutableStateFlow(AddMaterialState(workOrder = savedStateHandle.get<String>("work_order")!!))

    val state: StateFlow<AddMaterialState> = _state
    val materialsCount = materialsDao.get().materialsCountFlow().map { it + 1 }
    val materialsPager by lazy {
        pager { materialsDao.get().getUploadMaterialsPagingSource() }
    }

    init {
        getAddRecordLock()
    }

    private fun getAddRecordLock() = viewModelScope.launch(Dispatchers.IO) {
        _state.update { copy(loading = true) }
        _state.update {
            copy(
                loading = false,
                addRecordLock = preferenceGateway.get().addRecordLock
            )
        }
    }

    private fun getUOMCodeSuggestions(uomCode: String): List<String> {
        return when (uomCode) {
            "KG" -> listOf("KG", "TON")
            "Meter" -> listOf("Meter", "KM")
            else -> listOf(uomCode, "KG", "TON", "Meter", "KM")
        }
    }

    infix fun addEvent(event: AddMaterialEvent) {
        when (event) {
            AddMaterialEvent.GetSuggestionsForRmCode -> getSuggestions()
            is AddMaterialEvent.OnRmQueryChanged -> onRmQueryChanged(event.query)
            is AddMaterialEvent.OnSearchForServerMaterial -> onSearchForServerMaterial(event.rmCode)
            is AddMaterialEvent.OnNoteChanged -> onNoteChanged(event.note)
            is AddMaterialEvent.OnQtyChanged -> onQtyChanged(qty = event.qty)
            is AddMaterialEvent.OnUOMCodeChanged -> onUOMCodeChanged(uomCode = event.uomCode)
            AddMaterialEvent.OnSaveClicked -> onSaveClicked()
            is AddMaterialEvent.OnSectionChanged -> onSectionChanged(event.newSection)
        }
    }

    private fun onUOMCodeChanged(uomCode: String) {
        _state.update { copy(selectedUomCode = uomCode, isSelectedUOMValid = uomCode.isNotBlank()) }
    }

    private fun onSectionChanged(selectedSection: AddMaterialSection) {
        _state.update { copy(selectedSection = selectedSection) }
    }

    private fun onSaveClicked() = viewModelScope.launch(Dispatchers.IO) {
        _state.update { validate() }
            .takeIf { it.value.isValid && !it.value.loading }
            ?.update { copy(loading = true) }
            ?.run {
                val metadata = preferenceGateway.get().recordMeta
                val inputs = MaterialInputs(qty = value.qty, note = value.note, value.workOrder)
                createUploadMaterial(
                    value.serverMaterial!!.copy(uomCode = value.selectedUomCode!!),
                    materialInputs = inputs,
                    meta = metadata!!
                )
            }?.let { materialsDao.get().insertUploadMaterial(it) }
            ?.also {
                _state.update {
                    copy(
                        loading = false,
                        qty = "",
                        note = "",
                        selectedUomCode = null
                    )
                }
            }

    }

    private fun onRmQueryChanged(query: String) {
        _state.update {
            copy(
                rmCodeQuery = query,
                serverMaterial = null,
                uomCodeSuggestions = null,
                selectedUomCode = null
            )
        }
    }

    private fun onQtyChanged(qty: String) {
        _state.update { copy(qty = qty, isQtyValid = qty.isNotBlank()) }
    }

    private fun onNoteChanged(note: String) {
        _state.update { copy(note = note) }
    }

    private fun onSearchForServerMaterial(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            copy(
                loading = true,
                rmCodeSuggestions = null,
                loadingSuggestions = false,
                rmCodeQuery = query
            )
        }
        serverMaterialDao.get().getItem(query).let {
            _state.update {
                val uomCodeSuggestions = getUOMCodeSuggestions(it.uomCode)
                copy(
                    loading = false,
                    serverMaterial = it,
                    uomCodeSuggestions = uomCodeSuggestions,
                    selectedUomCode = uomCodeSuggestions.first()
                )
            }
        }
    }

    private fun getSuggestions() = viewModelScope.launch(Dispatchers.IO) {
        if (state.value.rmCodeQuery == state.value.serverMaterial?.rmCode) return@launch;
        _state.update {

            takeIf { rmCodeQuery.isNotBlank() }
                ?.also { _state.update { copy(loadingSuggestions = true) } }
                ?.let { pager { serverMaterialDao.get().getRMCodes(rmCodeQuery) } }
                ?.let { copy(loadingSuggestions = false, rmCodeSuggestions = it) }
                ?: copy(rmCodeSuggestions = null, loadingSuggestions = false)
        }
    }
}

fun <T> MutableStateFlow<T>.update(update: T.() -> T): MutableStateFlow<T> {
    return apply { value = value.update() }

}

sealed class AddMaterialEvent {
    data class OnRmQueryChanged(val query: String) : AddMaterialEvent()
    data class OnQtyChanged(val qty: String) : AddMaterialEvent()
    data class OnUOMCodeChanged(val uomCode: String) : AddMaterialEvent()
    data class OnNoteChanged(val note: String) : AddMaterialEvent()
    data class OnSectionChanged(val newSection: AddMaterialSection) : AddMaterialEvent()
    data class OnSearchForServerMaterial(val rmCode: String) : AddMaterialEvent()
    object GetSuggestionsForRmCode : AddMaterialEvent()
    object OnSaveClicked : AddMaterialEvent()
}

enum class AddMaterialSection { SearchInputs, MaterialInputs }
data class AddMaterialState(
    val loading: Boolean = false,
    val loadingSuggestions: Boolean = false,
    val rmCodeSuggestions: Pager<Int, String>? = null,
    val uomCodeSuggestions: List<String>? = null,
    val selectedUomCode: String? = null,
    val isSelectedUOMValid: Boolean = true,
    val serverMaterial: ServerMaterial? = null,
    val rmCodeQuery: String = "",
    val qty: String = "",
    val isQtyValid: Boolean = true,
    val note: String = "",
    val addRecordLock: Boolean = true,
    val workOrder: String = "",
    val selectedSection: AddMaterialSection = AddMaterialSection.SearchInputs
) {
    val isValid: Boolean
        get() = isQtyValid && isSelectedUOMValid

    fun validate() = copy(
        isQtyValid = qty.isNotBlank(),
        isSelectedUOMValid = selectedUomCode != null
    )

}