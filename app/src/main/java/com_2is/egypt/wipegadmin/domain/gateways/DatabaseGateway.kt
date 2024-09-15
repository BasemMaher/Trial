package com_2is.egypt.wipegadmin.domain.gateways

import androidx.paging.PagingSource
import androidx.room.*
import com_2is.egypt.wipegadmin.entites.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

import org.intellij.lang.annotations.Language

@TypeConverters(RecordStateConverter::class)
@Database(
    entities = [Item::class, Record::class, ServerMaterial::class, UploadMaterial::class],
    version = 7,
    exportSchema = false
)
abstract class ItemsDatabase : RoomDatabase() {
    abstract val itemsDao: ItemsDao
    abstract val serverMaterialDao: ServerMaterialDao
    abstract val materialDao: UploadMaterialDao
    abstract val recordsDao: RecordsDao
}

object RecordStateConverter {
    @TypeConverter
    @JvmStatic
    //fun fromJson(json: String): UploadState = Json.decodeFromString(json)
    fun fromJson(json: String): UploadState = fromJson(json)

    @JvmStatic
    @TypeConverter
   // fun toJson(uploadState: UploadState): String = Json.encodeToString(uploadState)
    fun toJson(uploadState: UploadState): String = toJson(uploadState)
}

@Dao
interface ItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Query("select distinct workOrder from items where workOrder like :workOrderQuery ||'%' ")
    fun getWorkOrders(workOrderQuery: String): PagingSource<Int, String>

    @Language("RoomSql")
    @Query(
        """select distinct operation,
        (select count(*)from items 
                where workOrder like :workOrderQuery and operation =i.operation)
            as itemsCount
            from items i where workOrder like :workOrderQuery  order by operation"""
    )
    suspend fun getOperationsByWorkOrder(workOrderQuery: String): List<Operation>

    @Query("select jobOrder from items where workOrder like :workOrderQuery and operation like :operationQuery")
    suspend fun getJopOrders(workOrderQuery: String, operationQuery: String): List<String>

    @Query("select * from items where workOrder like :workOrderQuery and operation like :operationQuery and jobOrder like :jopOrderQuery")
    suspend fun getItem(
        workOrderQuery: String,
        operationQuery: String,
        jopOrderQuery: String
    ): Item

    @Query("delete from items")
    suspend fun deleteAll()

    @Query("select count(*) from items ")
    suspend fun getSyncedCount(): Int

    @Update
    suspend fun updateItem(item: Item)
}

@Dao
interface ServerMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(serverMaterials: List<ServerMaterial>)

    @Query("select distinct rmCode from server_material where rmCode like :rmCodeQuery ||'%' ")
    fun getRMCodes(rmCodeQuery: String): PagingSource<Int, String>

    @Query("select * from server_material where rmCode =:rmCodeQuery")
    suspend fun getItem(rmCodeQuery: String): ServerMaterial

    @Query("select count(*) from server_material ")
    suspend fun getCount(): Int

    @Query("delete from server_material ")
    suspend fun deleteAll()

}

@Dao
interface RecordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Query("select count(*) from record")
    fun recordsCountFlow(): Flow<Int>

    @Query("select count(*) from record")
    fun recordsCount(): Int

    @Query("select * from record ORDER BY Serial desc")
    fun getRecordsPagingSource(): PagingSource<Int, Record>

    @Query("select * from record ")
    suspend fun getRecords(): List<Record>

    @Query("select * from record where itemCode = 'manual' ")
    fun getManualRecords(): Flow<List<Record>>

    @Query("delete from record")
    suspend fun deleteAllRecords()

    @Query("delete from sqlite_sequence where name='Record'")
    suspend fun clearPrimaryKey()
    @Query("UPDATE record SET version = 2")
    suspend fun editVersionFormula()

    @Transaction
    suspend fun deleteAll() = recordsCount().also {
         //  editVersionFormula()
           deleteAllRecords()
           clearPrimaryKey()
    }


}

@Dao
interface UploadMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadMaterial(material: UploadMaterial)

    @Update
    suspend fun updateUploadMaterial(material: UploadMaterial)

    @Query("select count(*) from upload_material")
    fun materialsCountFlow(): Flow<Int>

    @Query("select count(*) from upload_material")
    fun materialsCount(): Int

    @Query("select * from upload_material ORDER BY serial desc")
    fun getUploadMaterialsPagingSource(): PagingSource<Int, UploadMaterial>

    @Query("select * from upload_material ")
    suspend fun getUploadMaterials(): List<UploadMaterial>


    @Query("delete from upload_material")
    suspend fun deleteAllMaterials()

    @Query("UPDATE upload_material SET version = 2 ")
    suspend fun editVersionMaterialFormula()

    @Query("delete from sqlite_sequence where name='upload_material'")
    suspend fun clearPrimaryKey()

    @Transaction
    suspend fun deleteAll() = materialsCount().also {
              //editVersionMaterialFormula()
              deleteAllMaterials()
              clearPrimaryKey()

    }


}