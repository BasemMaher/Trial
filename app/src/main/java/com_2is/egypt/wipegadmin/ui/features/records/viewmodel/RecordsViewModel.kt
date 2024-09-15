package com_2is.egypt.wipegadmin.ui.features.records.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com_2is.egypt.wipegadmin.domain.gateways.RecordsDao
import com_2is.egypt.wipegadmin.domain.repositories.UploadItemRepository
import com_2is.egypt.wipegadmin.entites.Record
import com_2is.egypt.wipegadmin.ui.core.pager
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val recordsDao: Lazy<RecordsDao>
) : ViewModel() {

    val recordsCount by lazy {
        recordsDao.get().recordsCountFlow()
    }
    private val _state by lazy {
        val pager = pager { recordsDao.get().getRecordsPagingSource() }
        MutableStateFlow(RecordsScreenState(recordsPager = pager))
    }
    val state: StateFlow<RecordsScreenState> = _state
}

data class RecordsScreenState(val loading: Boolean = false, val recordsPager: Pager<Int, Record>)