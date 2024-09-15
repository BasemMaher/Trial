package com_2is.egypt.wipegadmin.ui.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.asFlow
import androidx.work.*
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.domain.repositories.UploadItemRepository
import com_2is.egypt.wipegadmin.entites.UploadState
import com_2is.egypt.wipegadmin.ui.WipEgAdminApp
import com_2is.egypt.wipegadmin.ui.features.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

enum class UploadType() {
    Records, Materials
}


@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val uploadItemRepository: UploadItemRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val ERROR_DATA_KEY = "error"
        const val RECORDS_COUNT_KEY = "records_count"
        const val ERRORS_COUNT_KEY = "errors_count"
        const val UPLOAD_TYPE_KEY = "upload_type"
        const val UPLOADED_COUNT_KEY = "uploaded_count"

    }

    override suspend fun doWork(): Result {

        return runCatching { startUpload() }
            .run {
                println(exceptionOrNull())
                if (isSuccess) {
                    val data = getOrThrow()
                    val errorsCount = data.getInt(ERRORS_COUNT_KEY, -1)
                    val uploadedCount = data.getInt(UPLOADED_COUNT_KEY, -1)
                    if (errorsCount > 0) {
                        Data.Builder().putString(
                            ERROR_DATA_KEY,
                            "$errorsCount Records have error, and $uploadedCount uploaded"
                        ).build().let(Result::failure)
                    } else
                        Result.success(data)
                } else {
                    Data.Builder().putString(ERROR_DATA_KEY, exceptionOrNull().toString())
                        .build()
                        .let(Result::failure)
                }
            }
    }

    private suspend fun startUpload(): Data {
        // val token = uploadItemRepository.fetchToken()
        val type = inputData.getString(UPLOAD_TYPE_KEY)?.let { UploadType.valueOf(it) }!!
        val items = when (type) {
            UploadType.Records -> uploadItemRepository.uploadRecords()
            UploadType.Materials -> uploadItemRepository.uploadMaterials()
        }
        var uploadedCount = 0
        var errorsCount = 0
        items
            .map { it.await() }
            .forEach { info ->
                if (info.state is UploadState.ErrorWithUpload)
                    errorsCount++
                if (info.state is UploadState.Uploaded)
                    uploadedCount++
                setForeground(
                    createForegroundInfo(
                        errorsCount = errorsCount,
                        uploadedCount = uploadedCount,
                        recordsCount = info.recordCount
                    )
                )
            }
        if (uploadedCount > 0)
            when (type) {
                UploadType.Records -> uploadItemRepository.lockRecording()
                UploadType.Materials -> uploadItemRepository.lockMaterialInserting()
            }


        sendCompleteNotification(
            errorsCount = errorsCount,
            uploadedCount = uploadedCount,
            recordsCount = errorsCount + uploadedCount
        )
        return Data.Builder()
            .putInt("uploaded_count", uploadedCount)
            .putInt("errors_count", errorsCount)
            .putInt("records_count", errorsCount + uploadedCount)
            .build()
    }

    private fun createForegroundInfo(
        errorsCount: Int,
        uploadedCount: Int,
        recordsCount: Int
    ): ForegroundInfo {
        val id = WipEgAdminApp.UPLOAD_NOTIFICATION_CHANNEL_ID
        val title = "uploading records"
        val cancel = "Cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())
        val progress =
            "$uploadedCount uploaded \n$errorsCount have error \nfrom $recordsCount records"

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(recordsCount, errorsCount + uploadedCount, false)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(3, notification)
    }

    private fun sendCompleteNotification(
        errorsCount: Int,
        uploadedCount: Int,
        recordsCount: Int
    ) {
        val id = WipEgAdminApp.UPLOAD_NOTIFICATION_CHANNEL_ID

        val progress =
            "$uploadedCount uploaded \n$errorsCount have error \nfrom $recordsCount records"
        val pendingIntent =
            Intent(applicationContext, MainActivity::class.java).let {
                PendingIntent.getActivity(applicationContext, 0, it, 0)
            }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle("Upload Complete")
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(2, notification)
    }
}

fun Context.launchUploadWorker(uploadType: UploadType): Flow<WorkInfo> =
    OneTimeWorkRequestBuilder<UploadWorker>()
        .setInputData(
            Data.Builder().putString(UploadWorker.UPLOAD_TYPE_KEY, uploadType.name).build()
        )
        .build().let { worker ->
            WorkManager.getInstance(this).run {
                enqueueUniqueWork(uploadType.name, ExistingWorkPolicy.KEEP, worker)
                getWorkInfoByIdLiveData(worker.id).asFlow()
            }
        }
