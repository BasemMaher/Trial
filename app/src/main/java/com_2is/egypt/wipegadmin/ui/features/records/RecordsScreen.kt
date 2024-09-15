package com_2is.egypt.wipegadmin.ui.features.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.*
import com.google.accompanist.insets.navigationBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.records.viewmodel.RecordsViewModel
import com_2is.egypt.wipegadmin.ui.services.UploadType
import com_2is.egypt.wipegadmin.ui.services.launchUploadWorker
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import kotlinx.coroutines.flow.Flow

@Composable
fun RecordsScreen( viewModel: RecordsViewModel) {
    val state = viewModel.state.collectAsState()
    val recordsCount by viewModel.recordsCount.collectAsState(initial = 1)
    val context = LocalContext.current
    var operationState by remember {
        mutableStateOf<Flow<WorkInfo>?>(null)
    }
    operationState?.let {
        HandleUploadWorkInfo(workInfo = it, isAdmin = true) {
            operationState = null
        }
    }
    Scaffold(floatingActionButton = {
        if (recordsCount > 0)
            ExtendedFloatingActionButton(text = { Text(stringResource(R.string.upload)) },
                onClick = { operationState = context.launchUploadWorker(UploadType.Records) })
    }) {
        val lazyRecordsItems = state.value.recordsPager.flow.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            items(lazyRecordsItems) { record ->
                if (record != null) {
                    RecordCard(record = record)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            if (recordsCount == 0) {
                item {

                    Text(
                        text = stringResource(R.string.no_records),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }


            }
        }
    }
}
