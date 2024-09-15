package com_2is.egypt.wipegadmin.ui.features.meta_data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.*
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel.MetaDataState
import com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel.MetaDataViewModel
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.ProgressDialog
import com_2is.egypt.wipegadmin.ui.sub_features.composables.StringField


@Composable
fun MetaDataScreen( metaDataViewModel: MetaDataViewModel) {
    val state by metaDataViewModel.state.collectAsState()
    Scaffold(
        Modifier
            .navigationBarsPadding()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            item { DateField(state, metaDataViewModel) }
            item {
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        PlantField(state, metaDataViewModel)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        VersionField(state, metaDataViewModel)
                    }
                }
            }
            item { HeaderNoteField(state, metaDataViewModel) }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    if (state.recordMetaChanged) {
                        SaveFAP(metaDataViewModel)

                    }
                }
            }
            //NewRecordFAP(homeViewModel, state)

        }
        if (state.loading) ProgressDialog()

    }
}

@Composable
private fun HeaderNoteField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.headerNote,
        onChange = metaDataViewModel::changeHeaderNote,
        isValidInput = state.metaValidator?.isHeaderNoteValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.header_note),
        isEnabled = true
    )
}

@Composable
private fun VersionField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.version,
        onChange = metaDataViewModel::changeVersion,
        isValidInput = state.metaValidator?.isVersionValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.version),
        isEnabled = true
    )
}

@Composable
private fun PlantField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.plant,
        onChange = metaDataViewModel::changePlant,
        isValidInput = state.metaValidator?.isPlantValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.plant),
        isEnabled = true
    )
}

@Composable
private fun AreaField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.area,
        onChange = metaDataViewModel::changeArea,
        isValidInput = state.metaValidator?.isAreaValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.area),
        isEnabled = true
    )
}

@Composable
private fun DateField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    Row {
        Box(modifier = Modifier.weight(1f)) {
            YearField(state, metaDataViewModel)
        }
        Box(modifier = Modifier.weight(1f)) {
            MonthField(state, metaDataViewModel)
        }
        Box(modifier = Modifier.weight(1f)) {
            AreaField(state, metaDataViewModel)
        }

    }
}

@Composable
private fun MonthField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.month,
        onChange = metaDataViewModel::changeMonth,
        isValidInput = state.metaValidator?.isMonthValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.month),
        isEnabled = true
    )
}

@Composable
private fun YearField(
    state: MetaDataState,
    metaDataViewModel: MetaDataViewModel
) {
    StringField(
        value = state.recordMeta.year,
        onChange = metaDataViewModel::changeYear,
        isValidInput = state.metaValidator?.isYearValid ?: true,
        padding = 8.dp,
        title = stringResource(R.string.year),
        isEnabled = true
    )
}

@Composable
private fun SaveFAP(
    metaDataViewModel: MetaDataViewModel
) {
    ExtendedFloatingActionButton(
        onClick = { metaDataViewModel.save() },
        modifier = Modifier
            .padding(2.dp),
        text = { Text(text = stringResource(R.string.save)) },
        icon = { Icon(Icons.Rounded.Done, null) }
    )
}


