package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun <T> Spinner(
    modifier: Modifier = Modifier,
    onSelect: (T) -> Unit,
    label: String,
    items: List<T>,
    itemText: (T) -> String,
    selected: (T) -> Boolean,
    key: (T) -> String,
    maxWeight: Boolean = true,
    isValid: Boolean = true,
) {
    var openSpinner by remember {
        mutableStateOf(false)
    }
    val height = items.size
        .let { if (it > 5) 5 else it }
        .dp.times(48 + 6)
    Column {
        TextButton(
            border = if (!isValid) BorderStroke(1.dp, MaterialTheme.colors.error) else null,
            onClick = {
                openSpinner = !openSpinner
            }) {
            Text(text = label, modifier = if (maxWeight) modifier.weight(1f) else modifier)
            Icon(Icons.Rounded.ArrowDropDown, null)
        }

        DropdownMenu(
            expanded = openSpinner,
            onDismissRequest = {},
            modifier = Modifier
                .wrapContentWidth()
                .size(
                    width = 400.dp,
                    height = height
                ),
            properties = PopupProperties(focusable = false)
        ) {
            Surface(
                modifier = Modifier
                    .size(
                        width = 400.dp,
                        height = height
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .animateContentSize()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(items.size, key = { key(items[it]) }) { index ->
                        val item = items[index]
                        val backgroundColor =
                            if (selected(item)) MaterialTheme.colors.secondary else MaterialTheme.colors.surface
                        val textColor =
                            if (selected(item)) Color.White else MaterialTheme.colors.onSurface
                        DropdownMenuItem(
                            modifier = Modifier.background(backgroundColor),
                            onClick = {
                                openSpinner = false
                                onSelect(item)
                            }) {
                            Text(
                                text = itemText(item),
                                color = textColor
                            )
                        }

                    }
                }

            }
        }
    }
}
