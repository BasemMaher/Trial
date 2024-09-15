package com_2is.egypt.wipegadmin.ui.features.materials

import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com_2is.egypt.wipegadmin.ui.features.materials.viewModel.MaterialsViewModel
import com_2is.egypt.wipegadmin.ui.sub_features.composables.UploadMaterialCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.google.accompanist.insets.navigationBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.services.UploadType
import com_2is.egypt.wipegadmin.ui.services.launchUploadWorker
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import kotlinx.coroutines.flow.Flow

@Composable
fun MaterialsScreen(viewModel: MaterialsViewModel) {
    val state = viewModel.state.collectAsState()
    val materials by viewModel.materialsCount.collectAsState(initial = 1)
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
        if (materials > 0)
            ExtendedFloatingActionButton(text = { Text(stringResource(R.string.upload)) },
                onClick = {

                    operationState = context.launchUploadWorker(UploadType.Materials)

                })
    }) {
        val lazyMaterialsItems = state.value.materialsPager.flow.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            items(lazyMaterialsItems) { material ->
                if (material != null) {
                    UploadMaterialCard(material = material, showUploadState = true)
                    Spacer(modifier = Modifier.height(16.dp))
                }


            }
            if (materials == 0) {
                item {
                    Text(
                        text = "No Materials",
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }


            }
        }
    }
}
