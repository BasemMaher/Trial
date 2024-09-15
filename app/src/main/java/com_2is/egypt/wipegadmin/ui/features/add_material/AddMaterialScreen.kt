package com_2is.egypt.wipegadmin.ui.features.add_material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.WorkInfo
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.AddMaterialEvent
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.AddMaterialSection
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.AddMaterialState
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.AddMaterialViewModel
import com_2is.egypt.wipegadmin.ui.services.UploadType
import com_2is.egypt.wipegadmin.ui.services.launchUploadWorker
import com_2is.egypt.wipegadmin.ui.sub_features.composables.*
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun AddMaterialScreen(viewModel: AddMaterialViewModel, navHController: NavHostController) {
    val state by viewModel.state.collectAsState()

    if (state.loading) ProgressDialog()
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsWithImePadding(),

        topBar = { AddMaterialTopBar(state, viewModel, navHController) }) {
        Column(Modifier.padding(it)) {
            SearchSection(viewModel, state, navHController)
            if (state.serverMaterial != null) InputsSection(viewModel, state, navHController)
        }

    }

}

@Composable
fun ColumnScope.SearchSection(
    viewModel: AddMaterialViewModel,
    state: AddMaterialState,
    navHController: NavHostController
) {
    when (state.selectedSection) {
        AddMaterialSection.MaterialInputs -> {
            WorkOrderBtn(navHController, state)
            Card(modifier = Modifier.padding(4.dp)) {
                TextButton(onClick = {
                    viewModel addEvent AddMaterialEvent.OnSectionChanged(AddMaterialSection.SearchInputs)
                }) {
                    Text(
                        text = stringResource(R.string.search_info),
                        modifier = Modifier.weight(1f),
                        style = TextStyle(fontSize = 12.sp)
                    )
                    Icon(Icons.Rounded.ArrowDropDown, null)
                }

            }
        }
        AddMaterialSection.SearchInputs -> {
            WorkOrderBtn(navHController, state)
            Box(modifier = Modifier.run {
                if (state.serverMaterial != null)
                    this else this.weight(1f)
            }) {
                SearchForRmCodeField(state, viewModel)
                if (state.rmCodeQuery.isBlank())
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Please write RM Code To Show Results",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
            }
            state.serverMaterial?.let {
                ServerMaterialCard(it)
            }
        }

    }

}

@Composable
private fun WorkOrderBtn(
    navHController: NavHostController,
    state: AddMaterialState
) {
    TextButton(onClick = { navHController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        PropertyText(
            label = "Work Order",
            value = state.workOrder,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun InputsSection(
    viewModel: AddMaterialViewModel,
    state: AddMaterialState,
    navHController: NavHostController
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    when (state.selectedSection) {
        AddMaterialSection.SearchInputs -> Card(modifier = Modifier.padding(4.dp)) {
            TextButton(onClick = {
                viewModel addEvent AddMaterialEvent.OnSectionChanged(AddMaterialSection.MaterialInputs)
            }) {
                Text(
                    text = stringResource(R.string.search_info),
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontSize = 12.sp)
                )
                Icon(Icons.Rounded.ArrowDropDown, null)
            }

        }
        AddMaterialSection.MaterialInputs -> {

            LazyColumn(
                state = scrollState,
            ) {
                state.serverMaterial?.let {
                    item { ServerMaterialCard(it) }
                    item { QtyField(state, viewModel, scope, scrollState) }
                    item { NoteField(state, viewModel, scope, scrollState) }
                    item { SaveBtn(state, focusManager, viewModel) }

                }

            }
        }

    }

}

@Composable
private fun SaveBtn(
    state: AddMaterialState,
    focusManager: FocusManager,
    viewModel: AddMaterialViewModel
) {
    if (!state.loading)
        ExtendedFloatingActionButton(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = { Text(text = "Save") },
            onClick = {
                focusManager.clearFocus()
                viewModel addEvent AddMaterialEvent.OnSaveClicked
            })
}

@Composable
private fun NoteField(
    state: AddMaterialState,
    viewModel: AddMaterialViewModel,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    StringField(
        padding = 4.dp,
        title = "Note", isValidInput = true,
        onChange = { viewModel addEvent AddMaterialEvent.OnNoteChanged(it) },
        value = state.note,
        isEnabled = !state.loading,
        modifier = Modifier.onFocusChanged {
            if (it.isFocused) {
                scope.launch {
                    scrollState.scrollToItem(2)
                }
            }
        }
    )
}

@Composable
private fun QtyField(
    state: AddMaterialState,
    viewModel: AddMaterialViewModel,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    Row {
        Box(modifier = Modifier.weight(2f)) {

            StringField(
                padding = 4.dp,
                title = "Qty",
                isValidInput = state.isQtyValid,
                onChange = { viewModel addEvent AddMaterialEvent.OnQtyChanged(it) },
                value = state.qty,
                keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Number),
                isEnabled = !state.loading, modifier = Modifier.onFocusChanged {
                    if (it.isFocused) {
                        scope.launch {
                            scrollState.scrollToItem(1)
                        }
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
        ) {

            Spinner(
                onSelect = { selected ->
                    viewModel addEvent AddMaterialEvent.OnUOMCodeChanged(
                        selected
                    )
                },
                label = "UOM Code:${state.selectedUomCode ?: "###"} ",
                items = state.uomCodeSuggestions!!,
                itemText = { it },
                selected = { state.selectedUomCode == it },
                isValid = state.isSelectedUOMValid,
                key = { it }
            )
        }
    }
}

@Composable
private fun AddMaterialTopBar(
    state: AddMaterialState,
    viewModel: AddMaterialViewModel,
    navHController: NavHostController
) {
    var openMaterialsDialog by remember { mutableStateOf(false) }
    val recordsCount by viewModel.materialsCount.collectAsState(initial = 0)
    Column {
        TopAppBar(title = { Text("Add Material") }, actions = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Material Serial $recordsCount") },
                onClick = {
                    openMaterialsDialog = true
                })
        })

    }
    if (openMaterialsDialog) {
        MessageDialog(dismiss = { openMaterialsDialog = false }) {
            val materialsLazyItems = viewModel.materialsPager.flow.collectAsLazyPagingItems()
            MaterialsDialogTitle()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                items(materialsLazyItems) {
                    if (it != null) {
                        UploadMaterialCard(material = it)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
                if (recordsCount == 1) {
                    item {
                        Text(
                            text = "No Materials",
                            Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            Row {
                TextButton(
                    onClick = { openMaterialsDialog = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.close), textAlign = TextAlign.Center)
                }
                val context = LocalContext.current
                var operationState by remember {
                    mutableStateOf<Flow<WorkInfo>?>(null)
                }
                operationState?.let {
                    HandleUploadWorkInfo(workInfo = it) { success ->
                        operationState = null
                        if (success) navHController.popBackStack()
                    }
                }
//                if (recordsCount > 1) TextButton(
//                    enabled = false,
//                    onClick = {
//                        operationState = context.launchUploadWorker(UploadType.Materials)
//
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                ) {
//                    Text("Upload", textAlign = TextAlign.Center)
//                }
            }
        }
    }
}

@Composable
private fun MaterialsDialogTitle() {
    Text(
        text = "Materials",
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )
}


@Composable
private fun SearchForRmCodeField(
    state: AddMaterialState,
    viewModel: AddMaterialViewModel
) {
    AutoCompleteTextField(
        suggestions = state.rmCodeSuggestions,
        label = "RM Code",
        onValueChange = { viewModel addEvent AddMaterialEvent.OnRmQueryChanged(it) },
        onSearch = { viewModel addEvent AddMaterialEvent.OnSearchForServerMaterial(it) },
        getSuggestions = { viewModel addEvent AddMaterialEvent.GetSuggestionsForRmCode },
        loading = state.loadingSuggestions,
        queryValue = state.rmCodeQuery,
    )
}