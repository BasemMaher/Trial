package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable


@Composable
 fun ClearIcon(
    onClick: () -> Unit

) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.Clear,
            null
        )
    }
}


