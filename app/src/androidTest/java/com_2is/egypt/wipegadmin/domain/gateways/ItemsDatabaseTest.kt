package com_2is.egypt.wipegadmin.domain.gateways

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com_2is.egypt.wipegadmin.entites.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

private const val RESPONSE = """ {
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
        }]}"""

@RunWith(AndroidJUnit4::class)
class ItemsDatabaseTest {
    private lateinit var itemsDao: ItemsDao
    private lateinit var db: ItemsDatabase
    private lateinit var itemsResponse: ItemsResponse

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ItemsDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        itemsDao = db.itemsDao

        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        itemsResponse = gson.fromJson(RESPONSE, ItemsResponse::class.java).run {
            val first = items.first()
            val testItems = mutableListOf<ServerItem>().apply {
                for (i in 1..itemsCount) {
                    add(first.copy(jobOrder = first.jobOrder + i.toString(),workOrder=first.workOrder+ i.toString()))

                }
            }
            Log.d("test", testItems.size.toString())
            copy(itemsCount, testItems)

        }


    }

    @Test
    fun writeUserAndReadInList() = runBlocking(Dispatchers.Unconfined) {
        println(itemsDao.getSyncedCount())
        itemsDao.insertAll(itemsResponse.items.toLocaleItems())
        println(itemsResponse.items.first())
        println(itemsResponse.items.last())
        println(itemsDao.getSyncedCount())

        println(itemsResponse.itemsCount)
        assert(itemsResponse.items.size == itemsDao.getSyncedCount())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}