package com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel

import com_2is.egypt.wipegadmin.entites.RecordMeta
import com_2is.egypt.wipegadmin.entites.RecordMetaValidator

data class MetaDataState(
    val loading: Boolean = false,
    val recordMetaChanged: Boolean = true,
    val metaValidator: RecordMetaValidator? = null,
    val recordMeta: RecordMeta = RecordMeta()
)