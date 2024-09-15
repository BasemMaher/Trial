package com_2is.egypt.wipegadmin.domain.gateways

import com.google.gson.Gson
import com_2is.egypt.wipegadmin.domain.core.base
import com_2is.egypt.wipegadmin.domain.core.path
import com_2is.egypt.wipegadmin.entites.*
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerState
import dagger.Lazy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


class ServiceGateway(preferences: PreferenceGateway, private val gson: Gson) {
    private var authBaseUrl = preferences.authURL.base
    private var itemsBaseUrl = preferences.itemsURL.base
    private var materialsURL = preferences.materialsURL.base
    private var uploadItemBaseUrl = preferences.uploadItemURL.base
    private var uploadMaterialBaseUrl = preferences.uploadMaterialURL.base
    private lateinit var authService: AuthService
    private lateinit var itemsService: ItemsService
    private lateinit var materialsService: MaterialService
    private lateinit var uploadItemService: UploadItemService
    private lateinit var uploadMaterialService: UploadMaterialService
    fun getAuthService(url: String): AuthService {
        if (url != authBaseUrl) {
            authService = buildAuthService(url)
            authBaseUrl = url
        }
        authService = if (!::authService.isInitialized) buildAuthService(url) else authService
        return authService
    }

    private fun buildAuthService(url: String) =
        Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthService::class.java)

    fun getItemsService(url: String): ItemsService {
        if (url != itemsBaseUrl) {
            itemsService = buildItemsService(url)
            itemsBaseUrl = url
        }
        itemsService = if (!::itemsService.isInitialized) buildItemsService(url) else itemsService
        return itemsService
    }

    fun getMaterialService(url: String): MaterialService {
        if (url != materialsURL) {
            materialsService = buildMaterialsService(url)
            materialsURL = url
        }
        materialsService =
            if (!::materialsService.isInitialized) buildMaterialsService(url) else materialsService
        return materialsService
    }

    fun getUploadMaterialService(url: String): UploadMaterialService {
        if (url != uploadMaterialBaseUrl) {
            uploadMaterialService = buildUploadMaterialService(url)
            uploadMaterialBaseUrl = url
        }
        uploadMaterialService =
            if (!::uploadMaterialService.isInitialized) buildUploadMaterialService(url) else uploadMaterialService
        return uploadMaterialService
    }

    fun getUploadItemService(url: String): UploadItemService {
        if (url != uploadItemBaseUrl) {
            uploadItemService = buildUploadItemService(url)
            uploadItemBaseUrl = url
        }
        uploadItemService =
            if (!::uploadItemService.isInitialized) buildUploadItemService(url) else uploadItemService
        return uploadItemService
    }

    private fun buildItemsService(url: String) =
        Retrofit.Builder().baseUrl(url)
            .run {
                val client = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build()
                client(client)
            }
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ItemsService::class.java)

    private fun buildMaterialsService(url: String) =
        Retrofit.Builder().baseUrl(url)
            .run {
                val client = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build()
                client(client)
            }
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MaterialService::class.java)

    private fun buildUploadItemService(url: String) =
        Retrofit.Builder().baseUrl(url)
            .run {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
                client(client)
            }
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(UploadItemService::class.java)

    private fun buildUploadMaterialService(url: String) =
        Retrofit.Builder().baseUrl(url)
            .run {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
                client(client)
            }
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(UploadMaterialService::class.java)

}

interface AuthService {

    @POST("{path}")
    suspend fun getAccessToken(
        @Path("path", encoded = false) path: String,
        @Body body: AuthBody
    ): ServerToken
}

@Singleton
class TokenProvider @Inject constructor(
    private val preferenceGateway: Lazy<PreferenceGateway>,
    private val serviceGateway: Lazy<ServiceGateway>
) {

    suspend fun getToken() = preferenceGateway.get()
        .getSavedState()
        .run { getSavedToken() ?: fetchNewToken() }

    private fun getSavedToken() = preferenceGateway.get().token
        ?.takeIf { token -> tokenExistAndValid(token) }

    private fun tokenExistAndValid(token: Token) = token.expireDate.after(Date())


    suspend fun ControllerState.fetchNewToken() =
        serviceGateway.get()
            .also { println("Auth base:${authURL.base}\n ${authURL.path}") }
            .getAuthService(authURL.base)
            .getAccessToken(authURL.path, toAuthBody())
            .also { println(toAuthBody()) }
            .toToken()
            .also { preferenceGateway.get().saveState(this, it) }
}

interface ItemsService {
    @GET("{path}")
    suspend fun getItems(
        @Path("path") path: String,
        @Header("Authorization") accessToken: String
    ): ItemsResponse
}

interface UploadItemService {
    @POST("{path}")
    suspend fun uploadItem(
        @Path("path") path: String,
        @Header("Authorization") accessToken: String,
        @Body record: RecordBody
    ):  RecordSubmitResponse
}

interface MaterialService {
    @GET("{path}")
    suspend fun getMaterials(
        @Path("path") path: String,
        @Header("Authorization") accessToken: String
    ): MaterialsResponse
}

interface UploadMaterialService {
    @POST("{path}")
    suspend fun uploadItem(
        @Path("path") path: String,
        @Header("Authorization") accessToken: String,
        @Body material: UploadMaterial
    ): RecordSubmitResponse
}
