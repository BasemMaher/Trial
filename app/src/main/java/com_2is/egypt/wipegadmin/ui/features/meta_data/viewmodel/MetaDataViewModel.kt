package com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com_2is.egypt.wipegadmin.domain.gateways.PreferenceGateway
import com_2is.egypt.wipegadmin.entites.RecordMetaValidator
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetaDataViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferences: Lazy<PreferenceGateway>
) : ViewModel() {
    private val _state = MutableStateFlow(MetaDataState())
    val state: StateFlow<MetaDataState> = _state

    init {
        getSavedRecordMeta()
    }

    private fun getSavedRecordMeta() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.run {
                value = value.copy(loading = true)
                val recordMeta = preferences.get().recordMeta
                value = value.copy(
                    loading = false,
                    recordMeta = recordMeta ?: value.recordMeta,
                    metaValidator = recordMeta?.let(::RecordMetaValidator),
                    recordMetaChanged = recordMeta == null
                )
            }
        }
    }

    fun changeVersion(version: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(version = version),
                recordMetaChanged = true,
            )
        }

    fun changeYear(year: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(year = year),
                recordMetaChanged = true,
            )
        }

    fun changeMonth(month: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(month = month),
                recordMetaChanged = true,
            )
        }

    fun changeHeaderNote(headerNote: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(headerNote = headerNote),
                recordMetaChanged = true,
            )
        }

    fun changeArea(area: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(area = area),
                recordMetaChanged = true,
            )
        }

    fun changePlant(plant: String) =
        _state.run {
            value = value.copy(
                recordMeta = value.recordMeta.copy(plant = plant),
                recordMetaChanged = true,
            )
        }

    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (state.value.loading) return@launch
        val validation = RecordMetaValidator(state.value.recordMeta)
        if (validation.isValid) {
            _state.run {
                value = value.copy(loading = true)
                preferences.get().recordMeta = value.recordMeta
                value = value.copy(loading = false, recordMetaChanged = false)
            }
        }

        _state.run { value = value.copy(metaValidator = validation) }
    }
}