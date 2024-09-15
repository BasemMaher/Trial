package com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel

import androidx.paging.Pager
import com_2is.egypt.wipegadmin.entites.*


enum class AddRecordSection {
    ItemsInfo, RecordInputs
}

data class AddRecordState(
    val manualRecord: Boolean = false,
    val openManualDialog: Boolean = false,
    val loadingWorkOrders: Boolean = false,
    val workOrderQuery: String = "",
    val operationQuery: Operation = Operation(),
    val jopOrderQuery: String = "",
    val workOrdersSuggestions: Pager<Int, String>? = null,
    val loading: Boolean = false,
    val operations: List<Operation> = listOf(),
    val jopOrders: List<String> = listOf(),
    val item: Item? = null,
    val recordMeta: RecordMeta = RecordMeta(),
    val selectedSection: AddRecordSection = AddRecordSection.ItemsInfo,
    val recordFormState: RecordFormState = RecordFormState()
    )

data class RecordFormState(
    val lotNum: String = "",
    val lotNumIsValid: Boolean = true,
    val drumNum: String = "",
    val drumNumberIsValid: Boolean = true,
    val qty: String = "",
    val qtyIsValid: Boolean = true,
    val note: String = "",
    val openUpdateDialog: Boolean = false,
    val openSelectOldRecordDialog: Boolean = false
) {
    fun validate() = copy(
        lotNumIsValid = lotNum.isNotBlank(),
        drumNumberIsValid = drumNum.isNotBlank(),
        qtyIsValid = qty.isNotBlank(),
    )

    val isValid
        get() = qtyIsValid && drumNumberIsValid && lotNumIsValid

    fun buildRecord(item: Item, recordMeta: RecordMeta) = recordMeta.toRecord(
        wo = item.workOrder,
        jo = item.jobOrder,
        lotNum = lotNum,
        drumNumber = drumNum,
        Qty = qty,
        note = note,
        classCode = item.classCode,
        itemCode = item.code
    )

    fun clear() = RecordFormState()


    fun fromRecord(record: Record): RecordFormState = RecordFormState(
        lotNum = record.lotNum,
        drumNum = record.drumNumber,
        qty = record.Qty,
        note = record.note,
    )


}
