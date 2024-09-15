package com_2is.egypt.wipegadmin.entites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class RecordMeta(
    val version: String = "",
    val area: String = "",
    val plant: String = "",
    val year: String = "",
    val month: String = "",
    val headerNote: String = "",
)

class RecordMetaValidator(
    recordMeta: RecordMeta
) {
    val isVersionValid: Boolean = recordMeta.version.isNotBlank()
    val isAreaValid: Boolean = recordMeta.area.isNotBlank()
    val isPlantValid: Boolean = recordMeta.plant.isNotBlank()
    val isYearValid: Boolean = recordMeta.year.isNotBlank()
    val isMonthValid: Boolean = recordMeta.month.isNotBlank()
    val isHeaderNoteValid: Boolean = recordMeta.headerNote.isNotBlank()
    val isValid =
        isAreaValid && isVersionValid && isPlantValid && isYearValid && isMonthValid && isHeaderNoteValid
}

fun RecordMeta.toRecord(
    wo: String,
    jo: String,
    itemCode: String,
    classCode: String,
    lotNum: String,
    drumNumber: String,
    Qty: String,
    note: String,
) = Record(
    version = version,
    area = area,
    plant = plant,
    year = year,
    month = month,
    headerNote = headerNote,
    Wo = wo,
    Jo = jo,
    itemCode = itemCode,
    classCode = classCode,
    drumNumber = drumNumber,
    lotNum = lotNum,
    note = note,
    Qty = Qty
)

// "item_code": "string",
//  "work_order": "string",
//  "job_order": "string",
//  "class_code": "string",
//  "p_version": "string",
//  "p_plant": "string",
//  "p_lot_number": "string",
//  "p_drum_number": "string",
//  "p_qty_km": "string",
//  "p_area": "string",
//  "p_hdr_notes": "string",
//  "p_dtl_notes": "string",
//  "p_date_year": "string",
//  "p_date_month": "string",
//  "p_serial_number": "string"
class RecordBody(
    @SerializedName("p_serial_number") @Expose val Serial: String,
    @SerializedName("p_version") @Expose val version: String,
    @SerializedName("p_area") @Expose val area: String,
    @SerializedName("p_plant") @Expose val plant: String,
    @SerializedName("p_date_year") @Expose val year: String,
    @SerializedName("p_date_month") @Expose val month: String,
    @SerializedName("p_hdr_notes") @Expose val headerNote: String,
    @SerializedName("work_order") @Expose val Wo: String,
    @SerializedName("job_order") @Expose val Jo: String,
    @SerializedName("item_code") @Expose val itemCode: String,
    @SerializedName("class_code") @Expose val classCode: String,
    @SerializedName("p_lot_number") @Expose val lotNum: String,
    @SerializedName("p_drum_number") @Expose val drumNumber: String,
    @SerializedName("p_qty_km") @Expose val Qty: String,
    @SerializedName("p_dtl_notes") @Expose val note: String,
)
class RecordSubmitResponse(
    @SerializedName("statusCode") @Expose val submitStatusCode: String,
    @SerializedName("message") @Expose val submitMessage: String,
    @SerializedName("serial_number") @Expose val SubmitSerialNumber: String,
)
@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val Serial: Long = 0,
    val version: String,
    val area: String,
    val plant: String,
    val year: String,
    val month: String,
    val headerNote: String,
    val Wo: String,
    val Jo: String,
    val itemCode: String,
    val classCode: String,
    val lotNum: String,
    val drumNumber: String,
    val Qty: String,
    val note: String,
    val state: UploadState = UploadState.NotUploaded
)

fun Record.toRecordBody() = RecordBody(
    Serial = Serial.toString(),
    version = version,
    area = area,
    plant = plant,
    year = year,
    month = month,
    headerNote = headerNote,
    Wo = Wo,
    Jo = Jo,
    itemCode = itemCode,
    classCode = classCode,
    drumNumber = drumNumber,
    lotNum = lotNum,
    note = note,
    Qty = Qty
)


class RecordProgressInfo(val recordCount: Int, val state: UploadState)