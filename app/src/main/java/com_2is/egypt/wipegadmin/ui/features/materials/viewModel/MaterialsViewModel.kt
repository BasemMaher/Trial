package com_2is.egypt.wipegadmin.ui.features.materials.viewModel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import com_2is.egypt.wipegadmin.domain.gateways.UploadMaterialDao
import com_2is.egypt.wipegadmin.entites.UploadMaterial
import com_2is.egypt.wipegadmin.ui.core.pager
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MaterialsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val materialsDao: Lazy<UploadMaterialDao>
) : ViewModel() {

    val materialsCount by lazy {
        materialsDao.get().materialsCountFlow()
    }
    private val _state by lazy {
        val pager = pager { materialsDao.get().getUploadMaterialsPagingSource() }
        MutableStateFlow(MaterialsScreenState(materialsPager = pager))
    }
    val state: StateFlow<MaterialsScreenState> = _state
}

data class MaterialsScreenState(
    val loading: Boolean = false,
    val materialsPager: Pager<Int, UploadMaterial>
)