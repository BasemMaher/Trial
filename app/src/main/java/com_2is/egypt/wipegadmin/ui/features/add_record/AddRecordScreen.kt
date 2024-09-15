package com_2is.egypt.wipegadmin.ui.features.add_record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.WorkInfo
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.Item
import com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel.AddRecordSection
import com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel.AddRecordState
import com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel.AddRecordViewModel
import com_2is.egypt.wipegadmin.ui.features.navigateToAddNewMaterial
import com_2is.egypt.wipegadmin.ui.services.UploadType
import com_2is.egypt.wipegadmin.ui.services.launchUploadWorker
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import com_2is.egypt.wipegadmin.ui.sub_features.composables.AutoCompleteTextField
import com_2is.egypt.wipegadmin.ui.sub_features.composables.ItemCard
import com_2is.egypt.wipegadmin.ui.sub_features.composables.Spinner
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

@Composable
fun AddRecordScreen(
    navHController: NavHostController,
    viewModel: AddRecordViewModel,
) {
    val state by viewModel.state.collectAsState()
    state.run {
        if (loading) ProgressDialog()
        if (openManualDialog) EnableManualModeDialog(viewModel)
    }
    Scaffold(
        Modifier.statusBarsPadding(),
        topBar = { AddRecordTopBar(navHController, viewModel, state) },
        bottomBar = {
            Spacer(
                Modifier
                    .navigationBarsWithImePadding()
                    .fillMaxWidth()
            )
        },
    )
    {
        Column(modifier = Modifier.padding(it)) {
            SearchForWorkOrdersField(state, viewModel)
            ItemInfoSection(state, viewModel, navHController)
            state.item?.let {
                when (state.selectedSection) {
                    AddRecordSection.ItemsInfo -> RecordInputSection(viewModel)
                    AddRecordSection.RecordInputs -> {
                        RecordForm(
                            recordMeta = state.recordMeta,
                            item = it,
                            onSave = viewModel::saveRecord,
                            state = state.recordFormState,
                            onStateChange = viewModel::onRecordFormStateChanged
                        )
                    }
                }


            }
        }

    }

}

@Composable
private fun RecordInputSection(viewModel: AddRecordViewModel) {
    Card {
        TextButton(onClick = {
            viewModel.changeSelectedSection(
                AddRecordSection.RecordInputs
            )
        }) {
            Text(
                text = stringResource(R.string.record_inputs),
                modifier = Modifier.weight(1f),
                style = TextStyle(fontSize = 12.sp)
            )
            Icon(Icons.Rounded.ArrowDropDown, null)
        }
    }
}

@Composable
private fun ItemInfoSection(
    state: AddRecordState,
    viewModel: AddRecordViewModel,
    navHController: NavHostController,

    ) {
    var openMaterialUploadedErrorDialog by remember {
        mutableStateOf(false)
    }
    if (openMaterialUploadedErrorDialog)
        MaterialsUploadedErrorDialog {
            openMaterialUploadedErrorDialog = false
        }
    when (state.selectedSection) {
        AddRecordSection.RecordInputs -> Card(modifier = Modifier.padding(4.dp)) {
            TextButton(onClick = { viewModel.changeSelectedSection(AddRecordSection.ItemsInfo) }) {
                Text(
                    text = stringResource(R.string.search_info),
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontSize = 12.sp)
                )
                Icon(Icons.Rounded.ArrowDropDown, null)
            }

        }
        AddRecordSection.ItemsInfo -> {
            val adMaterialLock by viewModel.addMaterialLocLD.asFlow().collectAsState(initial = true)
            state.operations
                .takeIf { it.isNotEmpty() }
                ?.let {
                    AddMaterialBtn(adMaterialLock) {
                        if (adMaterialLock) {
                            openMaterialUploadedErrorDialog = true
                        } else
                            navHController.navigateToAddNewMaterial(state.workOrderQuery)
                    }
                    OperationSpinner(viewModel, state)

                }
            if (state.workOrderQuery.isBlank())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.please_write_wo),
                        modifier = Modifier.align(Center)
                    )
                }
            state.jopOrders
                .takeIf { it.isNotEmpty() }
                ?.let { JobOrderSpinner(state, viewModel) }
            state.item?.let {
                ItemCard(item = it)

            }


        }
    }
}

@Composable
private fun AddMaterialBtn(
    openMaterialUploadedErrorDialog: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text = "Add Material Record")
    }
}

@Composable
private fun AddRecordTopBar(
    navHController: NavHostController,
    viewModel: AddRecordViewModel,
    state: AddRecordState
) {
    var openRecordsDialog by remember {
        mutableStateOf(false)
    }
    val recordsCount by viewModel.recordsCount.collectAsState(initial = 0)

        TopAppBar(title = {
            Text(
                stringResource(R.string.add_record)
            )
        }, actions = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Record Serial $recordsCount") },
                onClick = {
                    openRecordsDialog = true

                })
        })


    if (openRecordsDialog) {
        MessageDialog(
            dismiss = { openRecordsDialog = false },
            modifier = Modifier.fillMaxHeight(.98f)
        ) {
            val lazyRecords = viewModel.recordsPager.flow.collectAsLazyPagingItems()
            Text(
                text = stringResource(R.string.records),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                items(lazyRecords) { record ->
                    if (record != null) {
                        RecordCard(record = record, enableInfo = false)
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
                if (recordsCount == 1) {
                    item {
                        Text(
                            text = stringResource(R.string.no_records),
                            Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            val manualRecords by viewModel.manualRecords.collectAsState(initial = listOf())
            var openManualErrorDialog by remember {
                mutableStateOf(false)
            }
            if (openManualErrorDialog) {
                ShowErrorDialog(
                    error = Exception(
                        manualRecords.joinToString(
                            "\n",
                            prefix = "Those Serials Are Manual Please Update Them to Upload\n"
                        ) { "Serial ${it.Serial}" })
                ) {
                    openManualErrorDialog = false
                }
            }
            Row {
                TextButton(
                    onClick = { openRecordsDialog = false },
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
//                        if (manualRecords.isEmpty()) {
//                            operationState = context.launchUploadWorker(UploadType.Records)
//                        } else {
//                            openManualErrorDialog = true
//                        }
//                    },
//
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
private fun EnableManualModeDialog(viewModel: AddRecordViewModel) {
    MessageDialog(dismiss = { viewModel.queryChanged("") }) {
        Text(
            text = stringResource(R.string.manual_mode_message),
            modifier = Modifier.padding(8.dp)
        )
        Row {
            TextButton(
                onClick = { viewModel.queryChanged("") },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.clear))
            }
            TextButton(onClick = {
                viewModel.enableManualRecord()
            }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.enable))
            }
        }
    }
}

@Composable
private fun JobOrderSpinner(
    state: AddRecordState,
    viewModel: AddRecordViewModel
) {
    Spinner(
        onSelect = { viewModel.jopOrderChanged(it) },
        label = "Jop Order: ${if (state.jopOrderQuery.isNotBlank()) state.jopOrderQuery else "#####"}",
        items = state.jopOrders,
        itemText = { it },
        selected = { state.jopOrderQuery == it },
        key = { it },
    )
}

@Composable
private fun OperationSpinner(
    viewModel: AddRecordViewModel,
    state: AddRecordState
) {
    Spinner(
        onSelect = {
            viewModel.operationChanged(it)
        },
        label = "Operation: ${if (state.operationQuery.operation.isNotBlank()) state.operationQuery.operation else "#####"}",
        items = state.operations,
        itemText = { it.operation + if (it.itemsCount > 0) " (${it.itemsCount})" else "" },
        selected = { it == state.operationQuery },
        key = { it.operation },
    )
}

@Composable
private fun ColumnScope.SearchForWorkOrdersField(
    state: AddRecordState,
    viewModel: AddRecordViewModel
) {
    AutoCompleteTextField(
        suggestions = state.workOrdersSuggestions,
        label = stringResource(R.string.work_order),
        onValueChange = { viewModel.queryChanged(it) },
        onSearch = { viewModel.searchForJopOrders(it) },
        getSuggestions = { viewModel.getSuggestions() },
        loading = state.loadingWorkOrders,
        queryValue = state.workOrderQuery,
        isNumber = state.workOrderQuery.length < 4
    )
}


