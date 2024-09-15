package com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com_2is.egypt.wipegadmin.domain.gateways.ItemsDao
import com_2is.egypt.wipegadmin.domain.gateways.PreferenceGateway
import com_2is.egypt.wipegadmin.domain.gateways.RecordsDao
import com_2is.egypt.wipegadmin.entites.*
import com_2is.egypt.wipegadmin.ui.core.pager
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.update
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val manualOperations = listOf(
    "Drawing-010",
    "Stranding -020",
    "Insulation -030",
    "Assembly -040",
    "Bedding -050",
    "Lead -052",
    "Armouring -060",
    "Taping -070",
    "Screening -080",
    "Sheathing -090",
)

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val itemsDao: Lazy<ItemsDao>,
    private val recordsDao: Lazy<RecordsDao>,
    private val preferenceGateway: Lazy<PreferenceGateway>
) : ViewModel() {
    private val _state = MutableStateFlow(AddRecordState())
    val state: StateFlow<AddRecordState> = _state
    val recordsCount = recordsDao.get().recordsCountFlow().map { it + 1 }
    val recordsPager by lazy {
        pager { recordsDao.get().getRecordsPagingSource() }
    }
    val manualRecords by lazy { recordsDao.get().getManualRecords() }
    val addMaterialLocLD by lazy { preferenceGateway.get().addMaterialLockLiveData }

    init {
        init()
    }

    private fun init() = viewModelScope.launch {
        _state.update { copy(loading = true) }
        _state.update {
            preferenceGateway.get()
                .run {
                    copy(
                        loading = false,
                        recordMeta = recordMeta!!,
                    )
                }
        }
    }


    fun queryChanged(query: String) = _state.run {
        value = value.copy(
            workOrderQuery = query,
            manualRecord = false,
            openManualDialog = false,
            operations = listOf(),
            jopOrders = listOf(),
            operationQuery = Operation(),
            jopOrderQuery = "",
            item = null
        )
    }

    fun getSuggestions() = viewModelScope
        .launch(Dispatchers.IO) {
            _state.run {
                if (value.workOrderQuery == value.item?.workOrder) return@launch
                value = value.takeIf { it.workOrderQuery.isNotBlank() }
                    ?.also { value = value.copy(loadingWorkOrders = true) }
                    ?.let {
                        pager {
                            itemsDao.get().getWorkOrders(state.value.workOrderQuery)
                        }
                    }
                    ?.let { value.copy(workOrdersSuggestions = it, loadingWorkOrders = false) }
                    ?: value.copy(workOrdersSuggestions = null, loadingWorkOrders = false)
            }
        }

    fun searchForJopOrders(query: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _state.run {
                value = value.copy(
                    loadingWorkOrders = false,
                    loading = true,
                    workOrdersSuggestions = null,
                    workOrderQuery = query,
                    jopOrderQuery = ""
                )

                itemsDao.get().getOperationsByWorkOrder(query)
                    .also { operations ->
                        value = value.copy(
                            operations = operations,
                            openManualDialog = operations.isEmpty(),
                            operationQuery = if (operations.size == 1) operations.first() else Operation(),
                        )
                    }
                    .let { if (it.size == 1) it.first() else null }
                    ?.let { itemsDao.get().getJopOrders(query, it.operation) }
                    .orEmpty()
                    .apply {
                        value = value.copy(
                            jopOrders = this,
                            jopOrderQuery = if (size == 1) first() else ""
                        )
                    }
                    .takeIf { jopOrders -> jopOrders.size == 1 }
                    ?.let { value.getFirstItem() }
                    .let { value = value.copy(item = it, loading = false) }


            }
        }
    }

    private suspend fun AddRecordState.getFirstItem() = itemsDao.get().getItem(
        workOrderQuery = workOrderQuery,
        operationQuery = operations.first().operation,
        jopOrderQuery
    )

    private fun getManualJobOrder(workOrderQuery: String, operation: Operation) =
        workOrderQuery + "/" + operation.operation.split("-")[1]

    fun enableManualRecord() = _state.run {
        val operations = manualOperations.map { Operation(it) }
        val jopOrderQuery = getManualJobOrder(value.workOrderQuery, operations.first())
        value = value.copy(
            manualRecord = true,
            workOrdersSuggestions = null,
            openManualDialog = false,
            operations = operations,
            operationQuery = operations.first(),
            item = manualItem(operations.first(), value.workOrderQuery, jopOrderQuery),
            jopOrderQuery = jopOrderQuery,
            jopOrders = listOf(jopOrderQuery)
        )
    }


    private fun manualItem(operation: Operation, workOrderQuery: String, jopOrderQuery: String) =
        Item(
            jobOrder = jopOrderQuery,
            classCode = "manual",
            code = "manual",
            desc = "manual",
            operation = operation.operation,
            size = "manual",
            volt = "manual",
            workOrder = workOrderQuery
        )

    fun operationChanged(operation: Operation) = _state.run {
        value = value.takeUnless { value.manualRecord }
            ?.updateOperation(operation)
            ?.also { getJopOrders(operation) }
            ?: value.getManualJopOrders(operation)

    }

    private fun AddRecordState.updateOperation(operation: Operation): AddRecordState = copy(
        operationQuery = operation,
        loading = true,
        jopOrders = listOf(),
        item = null
    )

    private fun AddRecordState.getManualJopOrders(
        operation: Operation
    ): AddRecordState {
        val jopOrderQuery = getManualJobOrder(workOrderQuery, operation)
        val item = manualItem(
            operation,
            workOrderQuery,
            jopOrderQuery
        )
        return copy(
            operationQuery = operation,
            jopOrderQuery = jopOrderQuery,
            jopOrders = listOf(jopOrderQuery),
            item = item
        )
    }

    private fun getJopOrders(operation: Operation) {
        viewModelScope.launch(Dispatchers.IO) {
            val items =
                itemsDao.get().getJopOrders(
                    workOrderQuery = state.value.workOrderQuery,
                    operationQuery = operation.operation
                )

            _state.run { value = value.copy(loading = false, jopOrders = items) }
            jopOrderChanged(items.let { if (it.size == 1) it.first() else null }.orEmpty())
        }
    }

    fun jopOrderChanged(jopOrderQuery: String) {
        if (state.value.manualRecord) return
        _state.run {
            value =
                value.copy(jopOrderQuery = jopOrderQuery, loading = true, item = null)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemsDao.get().getItem(
                workOrderQuery = state.value.workOrderQuery,
                operationQuery = state.value.operationQuery.operation,
                jopOrderQuery
            )

            _state.run { value = value.copy(loading = false, item = item) }
        }
    }

    fun saveRecord(record: Record) {
        viewModelScope.launch {
            _state.run { value = value.copy(loading = true) }
            recordsDao.get().insertRecord(record)
            _state.run { value = value.copy(loading = false) }
        }
    }

    fun changeSelectedSection(section: AddRecordSection) {
        _state.run { value = value.copy(selectedSection = section) }
    }

    fun onRecordFormStateChanged(formState: RecordFormState) =
        _state.run { value = value.copy(recordFormState = formState) }
}


