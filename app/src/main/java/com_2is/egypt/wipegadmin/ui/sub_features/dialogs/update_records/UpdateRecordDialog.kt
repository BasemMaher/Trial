package com_2is.egypt.wipegadmin.ui.sub_features.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.Record
import com_2is.egypt.wipegadmin.entites.UploadState
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.update_records.UpdateRecordState
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.update_records.UpdateRecordViewModel
import com_2is.egypt.wipegadmin.ui.sub_features.composables.PropertyText
import com_2is.egypt.wipegadmin.ui.sub_features.composables.UploadStateIcon


@Composable
fun UpdateRecordDialog(
    viewModel: UpdateRecordViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onRecordSelected: (Record) -> Unit
) {
    val state = viewModel.state.collectAsState()
    MessageDialog(dismiss = onDismiss) {
        Text(
            text = stringResource(R.string.select_record),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h5
        )
        if (state.value == UpdateRecordState.Loading) {
            CircularProgressIndicator()
        } else if (state.value is UpdateRecordState.RecordsState) {
            val recordsState = state.value as UpdateRecordState.RecordsState
            val lazyRecords = recordsState.pager.flow.collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(lazyRecords, key = { record -> record.Serial }) { record ->
                    if (record != null) {
                        UpdateRecordCard(
                            record,
                            recordsState.selectedRecord,
                            modifier = Modifier.clickable {
                                viewModel.selectRecord(record = record)
                            })

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onDismiss() }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(
                    onClick = {
                        onRecordSelected(recordsState.selectedRecord!!)
                        viewModel.selectRecord(null)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = recordsState.selectedRecord != null
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}

@Composable
fun RecordCard(record: Record, enableInfo: Boolean = true) {

    Row {
        UpdateRecordCard(
            record = record, selectedRecord = null,
            modifier = Modifier.weight(1f)
        )
        if (enableInfo) UploadStateIcon(record.state)

    }
}


@Composable
private fun UpdateRecordCard(
    record: Record,
    selectedRecord: Record?,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (record == selectedRecord) MaterialTheme.colors.secondary else Color.White
    val isManual = record.itemCode == "manual"
    Card(
        backgroundColor = backgroundColor,
        modifier = modifier
            .fillMaxWidth(.9f),
        border = BorderStroke(
            1.dp, if (isManual)
                MaterialTheme.colors.error
            else
                Color.White
        )
    ) {
        Row {
            Surface(
                elevation = 8.dp,
                modifier = Modifier
                    .wrapContentWidth(),
                shape = MaterialTheme.shapes.medium,
                color = if (isManual)
                    MaterialTheme.colors.error
                else
                    Color.LightGray
            ) {

                Text(
                    text = if (isManual) "manual: ${record.Serial}" else record.Serial.toString(),
                    modifier = Modifier
                        .padding(8.dp),
                    color = if (isManual)
                        Color.White
                    else
                        Color.Black
                )
            }

            Column(modifier = Modifier.padding(4.dp)) {
                PropertyText(label = stringResource(R.string.job_order), value = record.Jo)
                PropertyText(label = stringResource(R.string.work_order), value = record.Wo)
                PropertyText(label = stringResource(R.string.qty), value = record.Qty)
                PropertyText(label = stringResource(R.string.note), value = record.note)
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        PropertyText(
                            label = stringResource(R.string.drum_number),
                            value = record.drumNumber
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PropertyText(
                            label = stringResource(id = R.string.lot_number),
                            value = record.lotNum
                        )
                    }
                }

            }
        }


    }
}
