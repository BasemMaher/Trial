package com_2is.egypt.wipegadmin.domain.dummy

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com_2is.egypt.wipegadmin.entites.ItemsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.IOException
import java.nio.charset.Charset

fun ComponentActivity.loadJSONFromAsset(fileName: String) = lifecycleScope.async(Dispatchers.IO) {
    return@async try {
        assets.open(fileName).use { stream ->
            stream
                .run { ByteArray(available()).apply { read(this) } }
                .let { buffer -> String(buffer, Charset.defaultCharset()) }
        }

    } catch (ex: IOException) {
        ex.printStackTrace()
        return@async null
    }
}

inline fun <reified T> String.fromJson(): T? {
    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    return gson.fromJson(this,T::class.java)
}