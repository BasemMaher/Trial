package com_2is.egypt.wipegadmin.ui.sub_features.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map

@Composable
fun AutoCompleteTextField(
    suggestions: Pager<Int, String>?,
    label: String,
    onValueChange: (String) -> Unit,
    getSuggestions: () -> Unit,
    queryValue: String,
    onSearch: (String) -> Unit,
    loading: Boolean,
    isNumber: Boolean = false
) {
    var query by remember {
        mutableStateOf(queryValue)
    }
    val focus = LocalFocusManager.current
    LaunchedEffect(key1 = query) {
        delay(300)

        getSuggestions()
    }
    Column {
        OutlinedTextField(
            value = queryValue,
            onValueChange = { text ->
                query = text
                onValueChange(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text(label) },
            trailingIcon = {
                if (queryValue.isNotBlank()) {
                    ClearIcon(onClick = {
                        onValueChange("")
                        query = ""
                        focus.clearFocus()
                    })
                } else Icon(Icons.Rounded.Search, null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(queryValue)
                    focus.clearFocus()
                })
        )

        suggestions?.let { pager -> SuggestionsList(pager, loading, onSearch, query, focus) }
    }
}

@Composable
private fun SuggestionsList(
    pager: Pager<Int, String>,
    loading: Boolean,
    onSearch: (String) -> Unit,
    query: String,
    focus: FocusManager
) {
    val lazyPagingItems = pager.flow
        .collectAsLazyPagingItems()
    if (loading && lazyPagingItems.itemCount > 0)
        SearchProgressBar()
    if (lazyPagingItems.itemCount > 0 || query.isNotBlank())
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            items(lazyPagingItems, key = { it }) { message ->
                if (message != null) {
                    Card(modifier = Modifier.padding(4.dp)) {
                        DropdownMenuItem(onClick = {
                            onSearch(message)
                            focus.clearFocus()
                        }, modifier = Modifier.height(30.dp)) {
                            Text(text = message)
                        }
                    }
                } else {
                    Text(text = "Loading...")
                }
            }
        }


}
