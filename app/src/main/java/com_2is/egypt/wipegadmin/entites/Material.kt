package com_2is.egypt.wipegadmin.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MaterialsResponse(
    @SerializedName("rmList")
    @Expose val items: List<ServerMaterial>,
)

@Entity(tableName = "server_material")
data class ServerMaterial(
    @PrimaryKey(autoGenerate = true) val serial: Long = 0,
    @SerializedName("rm_code") @Expose val rmCode: String,
    @SerializedName("description") @Expose val description: String,
    @SerializedName("primary_uom_code") @Expose val uomCode: String,
)

@Entity(tableName = "upload_material")
data class UploadMaterial(
    @SerializedName("p_serial_number") @Expose
    @PrimaryKey(autoGenerate = true) val serial: Long = 0,
    @SerializedName("p_rm_code") @Expose val rmCode: String,
    @SerializedName("p_uom") @Expose val uomCode: String,
    @SerializedName("p_version") @Expose val version: String,
    @SerializedName("p_area") @Expose val area: String,
    @SerializedName("p_plant") @Expose val plant: String,
    @SerializedName("p_date_year") @Expose val year: String,
    @SerializedName("p_date_month") @Expose val month: String,
    @SerializedName("p_hdr_notes") @Expose val headerNote: String,
    @SerializedName("work_order") @Expose val Wo: String,
    @SerializedName("p_qty") @Expose val qty: String,
    @SerializedName("p_dtl_notes") @Expose val note: String,
    val uploadState: UploadState = UploadState.NotUploaded
)

/*2is egypt
‏{
 "p_rm_code": "string",
   "p_uom": "string",
     "p_version": "string",
        "p_plant": "string",
          "p_qty": "string",
            "p_area": "string",
             "p_hdr_notes": "string",
              "p_dtl_notes": "string",
               "p_date_year": "string",
                 "p_date_month": "string",
                    "p_serial_number": "string"

                     }‏
*/
data class MaterialInputs(
    val qty: String,
    val note: String,
    val workOrder: String,
)

fun createUploadMaterial(
    serverMaterial: ServerMaterial,
    meta: RecordMeta,
    materialInputs: MaterialInputs
) = UploadMaterial(
    qty = materialInputs.qty,
    note = materialInputs.note,
    headerNote = meta.headerNote,
    month = meta.month,
    year = meta.year,
    plant = meta.plant,
    area = meta.area,
    version = meta.version,
    rmCode = serverMaterial.rmCode,
    uomCode = serverMaterial.uomCode,
    Wo = materialInputs.workOrder
)

//  {
//            "rm_code": "ST16",
//            "description": "Steel Tape for Armouring-0.5x50-",
//            "primary_uom_code": "KG"
//        },