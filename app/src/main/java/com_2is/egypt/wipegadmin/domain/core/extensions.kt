package com_2is.egypt.wipegadmin.domain.core

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(): Date? =
    SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz", Locale.getDefault())
        .parse(this)

typealias URL = String

//get path from url
val URL.path
    get() = substring(indexOf('/', startIndex = 8) + 1, length)

val URL.base
    get() = substring(0, indexOf("/", startIndex = 8) + 1)