package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PropertyText(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.caption.copy(color = Color.Gray)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2
        )
    }
}


