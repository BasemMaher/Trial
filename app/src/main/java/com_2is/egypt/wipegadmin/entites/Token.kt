package com_2is.egypt.wipegadmin.entites

import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com_2is.egypt.wipegadmin.domain.core.toDate
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerState
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */

data class AuthBody(
    @SerializedName("username")
    @Expose val username: String,
    @SerializedName("password")
    @Expose val password: String,
    @SerializedName("grant_type")
    @Expose val grantType: String,
)

fun ControllerState.toAuthBody() =
    AuthBody(username = userName, password = password, grantType = grantType)

data class ServerToken(
    @SerializedName("access_token")
    @Expose val access_token: String,
    @SerializedName("token_type")
    @Expose val token_type: String,
    @SerializedName("expires_in")
    @Expose val expires_in: Int,
    @SerializedName("statusCode")
    @Expose val statusCode: String,
    @SerializedName("message")
    @Expose val message: String,
    @SerializedName(".issued")
    @Expose val issued: String,
    @SerializedName(".expires")
    @Expose val expires: String
)

fun ServerToken.toToken() = Token(
    accessToken = "$token_type $access_token",
    expireDate = expires.toDate()!!
)

data class Token constructor(val accessToken: String, val expireDate: Date)

