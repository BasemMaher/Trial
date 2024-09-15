package com_2is.egypt.wipegadmin.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ItemsResponse(
    @SerializedName("joItemsCount")
    @Expose val itemsCount: Int,
    @SerializedName("joitems")
    @Expose val items: List<ServerItem>,
)


data class ServerItem(
    @SerializedName("item_code")
    @Expose
    val code: String,
    @SerializedName("work_order")
    @Expose val workOrder: String,
    @SerializedName("job_order")
    @Expose val jobOrder: String,
    @SerializedName("class_code")
    @Expose val classCode: String,
    @SerializedName("operation")
    @Expose val operation: String,
    @SerializedName("item_desc")
    @Expose val desc: String,
    @SerializedName("item_size")
    @Expose val size: String,
    @SerializedName("item_volt")
    @Expose val volt: String,
)

fun List<ServerItem>.toLocaleItems() = map { it.toItem() }
private fun ServerItem.toItem() = Item(
    jobOrder = jobOrder,
    classCode = classCode,
    code = classCode,
    desc = desc,
    operation = operation,
    size = size,
    volt = volt,
    workOrder = workOrder
)

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val primaryKey:Long=0,
    val jobOrder: String,
    val code: String,
    val workOrder: String,
    val classCode: String,
    val operation: String,
    val desc: String,
    val size: String,
    val volt: String,
)
class Operation(val operation: String="", val itemsCount: Int=0)
