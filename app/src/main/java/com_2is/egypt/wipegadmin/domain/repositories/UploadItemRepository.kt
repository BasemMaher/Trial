package com_2is.egypt.wipegadmin.domain.repositories

import com_2is.egypt.wipegadmin.domain.core.base
import com_2is.egypt.wipegadmin.domain.core.path
import com_2is.egypt.wipegadmin.domain.gateways.*
import com_2is.egypt.wipegadmin.entites.*
import dagger.Lazy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

private const val UPLOAD_SUCCESS_MESSAGE = "Oracle server: 1 Record inserted"


@Singleton
class UploadItemRepository @Inject constructor(
    private val itemsDao: Lazy<RecordsDao>,
    private val materialsDao: Lazy<UploadMaterialDao>,
    private val serviceGateway: Lazy<ServiceGateway>,
    private val preferences: Lazy<PreferenceGateway>,
    private val tokenProvider: Lazy<TokenProvider>
) {
    suspend fun uploadRecords() = withContext(Dispatchers.IO) {
        val uploadItemURL = preferences.get().uploadItemURL
        println(uploadItemURL)
        val service = serviceGateway.get().getUploadItemService(uploadItemURL.base)
        val token = tokenProvider.get().getToken()
        uploadRecords(service, uploadItemURL, token)
        //testUpload()
    }

    suspend fun uploadMaterials() = withContext(
        Dispatchers.IO
    ) {
        val uploadMaterialURl = preferences.get().uploadMaterialURL
        println(uploadMaterialURl)
        val service = serviceGateway.get().getUploadMaterialService(uploadMaterialURl.base)
        val token = tokenProvider.get().getToken()
        uploadMaterials(service, uploadMaterialURl, token)
    }

    private suspend fun uploadMaterials(
        service: UploadMaterialService,
        uploadItemURL: String,
        token: Token
    ) = withContext(Dispatchers.IO) {
        val records = materialsDao.get().getUploadMaterials()
        val recordsCount = records.size
        records
            .filter { it.uploadState == UploadState.NotUploaded || it.uploadState is UploadState.ErrorWithUpload }
            .map { mate -> uploadMaterialAsync(mate, service, uploadItemURL, token, recordsCount) }

    }

    private suspend fun testUpload(): Flow<RecordProgressInfo> {
        val records = itemsDao.get().getRecords()

        return records.asFlow().map { record ->
            itemsDao.get().updateRecord(record.copy(state = UploadState.Uploading))
            delay(100)
            val state = if (record.Serial.mod(2) == 0) {
                UploadState.Uploaded("Success")
            } else {
                UploadState.ErrorWithUpload("Test Error Message")
            }
            itemsDao.get().updateRecord(record.copy(state = state))
            RecordProgressInfo(records.size, state)
        }
    }

    private suspend fun uploadRecords(
        service: UploadItemService,
        uploadItemURL: String,
        token: Token
    ) = withContext(Dispatchers.IO) {
        val records = itemsDao.get().getRecords()
        val recordsCount = records.size
        records
            .filter { it.state == UploadState.NotUploaded || it.state is UploadState.ErrorWithUpload }
            .map { record -> uploadItemAsync(record, service, uploadItemURL, token, recordsCount) }

    }

    private fun CoroutineScope.uploadItemAsync(
        record: Record,
        service: UploadItemService,
        uploadItemURL: String,
        token: Token,
        recordsCount: Int
    ) = async {
        println(record)
        itemsDao.get().updateRecord(record.copy(state = UploadState.Uploading))
        kotlin.runCatching { uploadRecord(service, uploadItemURL, token, record) }
            .let(::buildStateFromResponse)
            .also { itemsDao.get().updateRecord(record.copy(state = it)) }
            .let { RecordProgressInfo(recordsCount, it) }
    }

    private fun CoroutineScope.uploadMaterialAsync(
        material: UploadMaterial,
        service: UploadMaterialService,
        uploadItemURL: String,
        token: Token,
        recordsCount: Int
    ) = async {
        println(material)
        materialsDao.get().updateUploadMaterial(material.copy(uploadState = UploadState.Uploading))
        kotlin.runCatching { uploadMaterial(service, uploadItemURL, token, material) }
            .let(::buildStateFromResponse)
            .also { materialsDao.get().updateUploadMaterial(material.copy(uploadState = it)) }
            .let { RecordProgressInfo(recordsCount, it) }
    }


    private fun buildStateFromResponse(response: Result<RecordSubmitResponse>) =
        response.takeIf { it.isSuccess }
            ?.let { handleResponseSuccess(it.getOrNull()!!) }
            ?: response.exceptionOrNull()
                .let { UploadState.ErrorWithUpload(it!!.message.orEmpty()) }


    private fun handleResponseSuccess(response: RecordSubmitResponse): UploadState = response
        .takeIf { it.submitMessage == UPLOAD_SUCCESS_MESSAGE }
        ?.let { UploadState.Uploaded(it.submitMessage) }
        ?: UploadState.ErrorWithUpload(response.submitMessage)


    private suspend fun uploadRecord(
        service: UploadItemService,
        uploadItemURL: String,
        token: Token,
        record: Record
    ): RecordSubmitResponse {

        return service.uploadItem(
            uploadItemURL.path,
            token.accessToken,
            record.toRecordBody()
        )

    }

    private suspend fun uploadMaterial(
        service: UploadMaterialService,
        uploadMaterialURL: String,
        token: Token,
        material: UploadMaterial
    ): RecordSubmitResponse {

        return service.uploadItem(
            uploadMaterialURL.path,
            token.accessToken,
            material
        )

    }

    suspend fun lockRecording() = withContext(Dispatchers.IO) {
        preferences.get().addRecordLock = true
    }

    suspend fun lockMaterialInserting() = withContext(Dispatchers.IO) {
        preferences.get().addMaterialLock = true
    }

    suspend fun fetchToken(): Token = tokenProvider.get().run {
        preferences.get().getSavedState().fetchNewToken()
    }

}