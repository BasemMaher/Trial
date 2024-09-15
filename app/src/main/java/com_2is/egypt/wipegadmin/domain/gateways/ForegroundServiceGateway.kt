package com_2is.egypt.wipegadmin.domain.gateways

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

import com_2is.egypt.wipegadmin.ui.services.ForegroundFetchService

sealed class FetchProgress(val type: String, val progress: Int) {
    object ConnectingToServer : FetchProgress("connecting To Server", 20)

    object GettingItems : FetchProgress("getting items", 40)
    class ItemsFetched(itemsCount: Int) :
        FetchProgress("$itemsCount items downloaded", 50)

    class DeletingOldItems(itemsCount: Int) : FetchProgress("deleting old items to insert $itemsCount new items", 60)

    class InsertingNewItems(itemsCount: Int) : FetchProgress("inserting new $itemsCount items", 80)

    class Done(itemsCount: Int) : FetchProgress("done, $itemsCount items inserted", 100)

    object Error : FetchProgress("Error", 100)
}

const val FETCH_PROGRESS_TYPE_EXTRA = "com_2is.egypt.wipegadmin.FETCH_PROGRESS_TYPE"
const val FETCH_PROGRESS_VALUE_EXTRA = "com_2is.egypt.wipegadmin.FETCH_PROGRESS_VALUE"

class ForegroundServiceGateway(private val context: Context) {
    fun updateProgress(progress: FetchProgress) {
        Intent(context, ForegroundFetchService::class.java).apply {
            putExtra(FETCH_PROGRESS_TYPE_EXTRA, progress.type)
            putExtra(FETCH_PROGRESS_VALUE_EXTRA, progress.progress)
            ContextCompat.startForegroundService(context, this)
        }

    }


}
