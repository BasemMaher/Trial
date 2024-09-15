package com_2is.egypt.wipegadmin.ui.sub_features.dialogs.update_records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com_2is.egypt.wipegadmin.domain.gateways.RecordsDao
import com_2is.egypt.wipegadmin.entites.Record
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateRecordViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val recordsDao: Lazy<RecordsDao>
) : ViewModel() {
    private val _state = MutableStateFlow<UpdateRecordState>(UpdateRecordState.Loading)
    val state: StateFlow<UpdateRecordState> = _state

    init {
        getRecords()
    }

    fun selectRecord(record: Record?) {
        _state.run {
            value = value.let { it as UpdateRecordState.RecordsState }.copy(selectedRecord = record)
        }
    }

    private fun getRecords() {
        viewModelScope.launch {
            _state.run {
                val pager = Pager(
                    config = PagingConfig(pageSize = 20)
                ) {
                    recordsDao.get().getRecordsPagingSource()
                }
                value = UpdateRecordState.RecordsState(pager)
            }
        }
    }
}

sealed class UpdateRecordState {
    object Loading : UpdateRecordState()
    data class RecordsState(
        val pager: Pager<Int, Record>,
        val selectedRecord: Record? = null
    ) : UpdateRecordState()
}