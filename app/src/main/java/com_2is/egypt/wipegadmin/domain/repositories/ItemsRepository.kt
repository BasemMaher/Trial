package com_2is.egypt.wipegadmin.domain.repositories

import androidx.room.Transaction
import com_2is.egypt.wipegadmin.domain.core.base
import com_2is.egypt.wipegadmin.domain.core.path
import com_2is.egypt.wipegadmin.domain.gateways.*
import com_2is.egypt.wipegadmin.entites.*
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerState
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ServerResult
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ItemsRepository @Inject constructor(
    private val itemsDao: Lazy<ItemsDao>,
    private val materialDao: Lazy<ServerMaterialDao>,
    private val serviceGateway: Lazy<ServiceGateway>,
    private val preferenceGateway: Lazy<PreferenceGateway>,
    private val foregroundService: Lazy<ForegroundServiceGateway>,
    private val tokenProvider: Lazy<TokenProvider>
) {
    suspend fun getSavedState() = preferenceGateway.get().getSavedState()
    suspend fun fetchToken(state: MutableStateFlow<ControllerState>) =
        tokenProvider.get().run {
            state.run {
                value = value.copy(loading = true)
                value = value.runCatching { fetchNewToken() }
                    .run { serverResult() }
                    .let { value.copy(loading = false, fetchResult = it) }
            }
        }

    private fun Result<Token>.serverResult() = if (isSuccess)
        ServerResult
            .TokenFetched(getOrThrow()
                .expireDate.let { "expire date $it" })
    else
        ServerResult
            .Error(exceptionOrNull()!!)

    suspend fun fetchRecords(state: MutableStateFlow<ControllerState>) =
        withContext(Dispatchers.IO) {
            state.runCatching {
                if (value.loading) return@withContext
                value = value.copy(loading = true)
                preferenceGateway.get().saveState(value)
                //  testForegroundService()
                val itemsResponse = value.run { getItems(itemsURL, getToken()) }
                updateRecords(itemsResponse.items.toLocaleItems())
                val insertedCount = itemsDao.get().getSyncedCount()

                if (itemsResponse.itemsCount != insertedCount)
                    throw  FailedToUpdateDatabaseException(itemsResponse.itemsCount - insertedCount)
                insertedCount
            }.let { result ->
                val fetchResult = fetchDataResult(result)
                state.value = state.value.copy(
                    loading = false,
                    fetchResult = fetchResult,
                    isLoggedIn = fetchResult is ServerResult.ItemsFetched,
                )
            }
        }

    suspend fun fetchMaterials(state: MutableStateFlow<ControllerState>) =
        withContext(Dispatchers.IO) {
            state.runCatching {
                if (value.loading) return@withContext
                value = value.copy(loading = true)
                preferenceGateway.get().saveState(value)
                //  testForegroundService()
                val itemsResponse = value.run { getMaterials(materialsURL, getToken()) }
                updateMaterials(itemsResponse.items)
                val insertedCount = materialDao.get().getCount()

                if (itemsResponse.items.size != insertedCount)
                    throw  FailedToUpdateDatabaseException(itemsResponse.items.size - insertedCount)
                insertedCount
            }.let { result ->
                val fetchResult = fetchDataResult(result)
                state.value = state.value.copy(
                    loading = false,
                    fetchResult = fetchResult,
                    isLoggedIn = fetchResult is ServerResult.ItemsFetched,
                )
            }
        }


    private suspend fun getItems(
        itemsURL: String,
        token: Token
    ): ItemsResponse = serviceGateway.get()
        .getItemsService(itemsURL.base)
        .getItems(itemsURL.path, token.accessToken)
        .apply { foregroundService.get().updateProgress(FetchProgress.ItemsFetched(itemsCount)) }

    private suspend fun getMaterials(
        materialsURL: String,
        token: Token
    ): MaterialsResponse = serviceGateway.get()
        .getMaterialService(materialsURL.base)
        .getMaterials(materialsURL.path, token.accessToken)
        .apply { foregroundService.get().updateProgress(FetchProgress.ItemsFetched(items.size)) }

    private suspend fun getToken(): Token {
        foregroundService.get().run {
            updateProgress(FetchProgress.ConnectingToServer)
            val token = tokenProvider.get().getToken()
            updateProgress(FetchProgress.GettingItems)
            return token
        }
    }

    private fun fetchDataResult(result: Result<Int>): ServerResult = result.run {
        foregroundService.get().updateProgress(
            if (isSuccess)
                FetchProgress.Done(getOrThrow())
            else FetchProgress.Error
        )
        if (isSuccess)
            ServerResult.ItemsFetched(getOrThrow())
        else
            ServerResult.Error(exceptionOrNull()!!)
    }

    @Transaction
    private suspend fun updateRecords(items: List<Item>) = itemsDao.get().run {
        foregroundService.get().updateProgress(FetchProgress.DeletingOldItems(items.size))
        deleteAll()
        foregroundService.get().updateProgress(FetchProgress.InsertingNewItems(items.size))
        items
            .sortedBy {
                val s = it.workOrder.split("/")
                if (s.size != 3) s.first() else s[1]
            }
            .reversed()
            .let { insertAll(it) }
    }

    @Transaction
    private suspend fun updateMaterials(items: List<ServerMaterial>) = materialDao.get().run {
        foregroundService.get().updateProgress(FetchProgress.DeletingOldItems(items.size))
        deleteAll()
        foregroundService.get().updateProgress(FetchProgress.InsertingNewItems(items.size))
        insertAll(items)
    }

    //    private suspend fun testForegroundService() {
//
//        foregroundService.updateProgress(FetchProgress.ConnectingToServer)
//        delay(2000)
//        foregroundService.updateProgress(FetchProgress.GettingItems)
//        delay(2000)
//        foregroundService.updateProgress(FetchProgress.ItemsFetched(100))
//        delay(2000)
//        foregroundService.updateProgress(FetchProgress.DeletingOldItems(100))
//        delay(2000)
//        foregroundService.updateProgress(FetchProgress.InsertingNewItems(100))
//        delay(2000)
//        foregroundService.updateProgress(FetchProgress.Done(100))
//
//        delay(2000)
//
//    }
}

