package com_2is.egypt.wipegadmin.ui.core

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource

const val PAGE_SIZE = 20

fun <T : Any> pager(pageSourceBuilder: () -> PagingSource<Int, T>) =
    Pager(config = PagingConfig(pageSize = PAGE_SIZE)) { pageSourceBuilder() }
