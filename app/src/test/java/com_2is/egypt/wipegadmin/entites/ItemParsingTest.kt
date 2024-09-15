package com_2is.egypt.wipegadmin.entites

import com.google.gson.GsonBuilder
import junit.framework.TestCase
import org.junit.Test


private const val ITEMS_RESPONSE = """ {
    "joItemsCount": 86859,
    "joitems": [
        {
            "item_code": "K00-S001-UTB-00-00",
            "work_order": "5113/2017/02",
            "job_order": "5113/2017/02/010",
            "class_code": "ACC_DIS",
            "operation": "Drawing",
            "item_desc": "AL WIRE 1350-O",
            "item_size": "1X3.46",
            "item_volt": ""
        },
        {
            "item_code": "CP1-TA01-U20-20-05",
            "work_order": "5124/2017/05",
            "job_order": "5124/2017/05/090",
            "class_code": "EGY_DIS",
            "operation": "Sheathing",
            "item_desc": "CU/PVC/PVC",
            "item_size": "1X240 RM",
            "item_volt": "0.6/1-KV"
        }]}"""

class ItemParsingTest : TestCase() {
    @Test
    fun `test parsing server items`() {
        //arrange
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        //act
        val serverItems = gson.fromJson(ITEMS_RESPONSE, ItemsResponse::class.java)
        //assert
        assertEquals(86859, serverItems.itemsCount)
        assertEquals(2, serverItems.items.size)
        val jsonItem = gson.toJson(serverItems.items[0])
        assert(!jsonItem.contains("synced"))

    }
}