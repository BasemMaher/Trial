package com_2is.egypt.wipegadmin.domain.gateways

import android.content.Context
import android.content.SharedPreferences

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com_2is.egypt.wipegadmin.domain.core.toDate
import com_2is.egypt.wipegadmin.entites.RecordMeta

import com_2is.egypt.wipegadmin.entites.Token
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PreferenceGateway @Inject constructor(@ApplicationContext context: Context) {
    private val pref by lazy { context.getSharedPreferences("app_pref", Context.MODE_PRIVATE) }


    suspend fun saveState(controllerState: ControllerState) =
        withContext(Dispatchers.IO) {
            userName = controllerState.userName
            password = controllerState.password
            grantType = controllerState.grantType
            itemsURL = controllerState.itemsURL
            authURL = controllerState.authURL
            uploadItemURL = controllerState.uploadRecordURL
            uploadMaterialURL = controllerState.uploadMaterialURL
            materialsURL = controllerState.materialsURL
        }

    suspend fun saveState(controllerState: ControllerState, token: Token) =
        withContext(Dispatchers.IO) {
            this@PreferenceGateway.token = token
            saveState(controllerState)
        }

    suspend fun getSavedState(): ControllerState = withContext(Dispatchers.IO) {
        val controllerState = ControllerState(
            userName = userName,
            password = password,
            authURL = authURL,
            itemsURL = itemsURL,
            grantType = grantType,
            uploadRecordURL = uploadItemURL,
            materialsURL = materialsURL,
            uploadMaterialURL = uploadMaterialURL,
            isLoggedIn = token != null
        )

        controllerState
    }

    companion object {
        private const val PACKAGE = "com_2is.egypt.wipegadmin"
        private const val TOKEN_KEY = "$PACKAGE.token"
        private const val ADD_RECORD_lOCK_KEY = "$PACKAGE.add_record_lock"
        private const val ADD_MATERIAL_lOCK_KEY = "$PACKAGE.add_material_lock"
        private const val USER_NAME_KEY = "$PACKAGE.user_name"
        private const val USER_NAME_DEF_VALUE = "WIP_STOCK"
        private const val PASSWORD_KEY = "$PACKAGE.password"
        private const val PASSWORD_DEF_VALUE = "12345678"
        private const val GRANT_TYPE_KEY = "$PACKAGE.grant_type"
        private const val GRANT_TYPE_DEF_VALUE = "password"
        private const val AUTH_URL_KEY = "$PACKAGE.auth_url"
        private const val AUTH_URL_DEF_VALUE = "http://dctaxsrv1:9191/api/v1/oracleauth/"
        private const val UPLOAD_ITEM_URL_KEY = "$PACKAGE.upload_item"
        private const val UPLOAD_MATERIAL_URL_KEY = "$PACKAGE.upload_material"
        private const val UPLOAD_ITEM_URL_DEF_VALUE =
            "http://dctaxsrv1:9191/api/v1/joitems_submit/"
        private const val UPLOAD_MATERIAL_URL_DEF_VALUE =
            "http://dctaxsrv1:9191/api/v1/rms_submit/"
        private const val ITEMS_URL_KEY = "$PACKAGE.items_url"
        private const val MATERIALS_URL_KEY = "$PACKAGE.materials_url"
        private const val RECORD_META_KEY = "$PACKAGE.record_meta"
        private const val ITEMS_URL_DEF_VALUE = "http://dctaxsrv1:9191/api/v1/joitems/"
        private const val MATERIALS_URL_DEF_VALUE = "http://dctaxsrv1:9191/api/v1/rms/"
        const val DEF_ACCESS_TOKEN =
            "bearer " +
                    "yYoGmW4BDBJSr6QgGyhPBwrj9UFhriTZKXJlyzWFLSpCuwwsSB7WGrvCjsckna7aeslXQcFMI4N6yEPbEjnFmjTrklchZVjhn9LifNUa30aHwm5bgnVqP9q7uIA6Vp0WfY5CRkkW5UoFZaUiETcio1WS79zFg962yZ9WqV-35DP5f5JKh_OYAjXOC9kJaEppQs0CaL14ZEp8JO1CT-EwisgV1Z-7-VIOeDbxkhBJHkdghmJN5wcEvPSOC2SYQLdrwxl-ye2mhz34AtuDJh5zkgDiUgmfq3lD8ZtX3zXuhIt4S3-LECTfkF1lSaIsGztWj8-XPaAin9sCbMeZvaYr9g"
        const val DEF_TOKEN_EXPIRE_DATE = "Fri, 26 Jul 2024 05:15:45 GMT"

    }

    var grantType: String
        get() = pref.getString(GRANT_TYPE_KEY, GRANT_TYPE_DEF_VALUE) ?: GRANT_TYPE_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(GRANT_TYPE_KEY, value)
        }
    var token: Token?
        get() {
            return pref.getString(TOKEN_KEY, null)
                ?.let { Gson().fromJson(it, Token::class.java) }
                ?: Token(
                    accessToken = DEF_ACCESS_TOKEN,
                    expireDate = DEF_TOKEN_EXPIRE_DATE.toDate()!!
                )
        }
        set(value) = pref.edit(commit = true) {
            putString(TOKEN_KEY, Gson().toJson(value))
        }
    var recordMeta: RecordMeta?
        get() {
            return pref.getString(RECORD_META_KEY, null)
                ?.let { Gson().fromJson(it, RecordMeta::class.java) }
        }
        set(value) = pref.edit(commit = true) {
            putString(RECORD_META_KEY, Gson().toJson(value))
        }

    var userName: String
        get() = pref.getString(USER_NAME_KEY, USER_NAME_DEF_VALUE) ?: USER_NAME_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(USER_NAME_KEY, value)
        }
    var password: String
        get() = pref.getString(PASSWORD_KEY, PASSWORD_DEF_VALUE) ?: PASSWORD_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(PASSWORD_KEY, value)
        }
    var authURL: String
        get() = pref.getString(AUTH_URL_KEY, AUTH_URL_DEF_VALUE) ?: AUTH_URL_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(AUTH_URL_KEY, value)
        }

    var itemsURL: String
        get() = pref.getString(ITEMS_URL_KEY, ITEMS_URL_DEF_VALUE) ?: ITEMS_URL_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(ITEMS_URL_KEY, value)
        }
    var materialsURL: String
        get() = pref.getString(MATERIALS_URL_KEY, MATERIALS_URL_DEF_VALUE)
            ?: MATERIALS_URL_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(MATERIALS_URL_KEY, value)
        }

    var uploadItemURL: String
        get() = pref.getString(UPLOAD_ITEM_URL_KEY, UPLOAD_ITEM_URL_DEF_VALUE)
            ?: UPLOAD_ITEM_URL_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(UPLOAD_ITEM_URL_KEY, value)
        }

    var uploadMaterialURL: String
        get() = pref.getString(UPLOAD_MATERIAL_URL_KEY, UPLOAD_MATERIAL_URL_DEF_VALUE)
            ?: UPLOAD_MATERIAL_URL_DEF_VALUE
        set(value) = pref.edit(commit = true) {
            putString(UPLOAD_MATERIAL_URL_KEY, value)
        }
    var addRecordLock: Boolean
        get() = pref.getBoolean(ADD_RECORD_lOCK_KEY, false)
        set(value) = pref.edit(commit = true) {
            putBoolean(ADD_RECORD_lOCK_KEY, value)
        }
    var addMaterialLock: Boolean
        get() = pref.getBoolean(ADD_MATERIAL_lOCK_KEY, false)
        set(value) = pref.edit(commit = true) {
            putBoolean(ADD_MATERIAL_lOCK_KEY, value)
        }
    val addMaterialLockLiveData: LiveData<Boolean> by lazy {
        object : SharedPreferenceLiveData<Boolean>(pref, ADD_MATERIAL_lOCK_KEY) {
            override val preferencesValue: Boolean
                get() = addMaterialLock
        }
    }


}

abstract class SharedPreferenceLiveData<T>(
    private val sharedPrefs: SharedPreferences,
    private val key: String
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == this.key) {
                value = preferencesValue
            }
        }
    abstract val preferencesValue: T

    override fun onActive() {
        super.onActive()
        value = preferencesValue
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}