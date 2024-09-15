package com_2is.egypt.wipegadmin.ui.features.add_record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.entites.Item
import com_2is.egypt.wipegadmin.entites.Record
import com_2is.egypt.wipegadmin.entites.RecordMeta
import com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel.RecordFormState
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.UpdateRecordDialog
import com_2is.egypt.wipegadmin.ui.sub_features.composables.StringField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RecordForm(
    recordMeta: RecordMeta,
    item: Item,
    onSave: (Record) -> Unit,
    state: RecordFormState,
    onStateChange: (RecordFormState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    state.run {
        if (openUpdateDialog)
            UpdateRecordDialog(
                onDismiss = { onStateChange(copy(openUpdateDialog = false)) }
            ) {
                state
                    .buildRecord(item, recordMeta).copy(Serial = it.Serial)
                    .let(onSave)
                    .also { onStateChange(clear()) }
            }
        if (openSelectOldRecordDialog)
            UpdateRecordDialog(
                onDismiss = { onStateChange(copy(openSelectOldRecordDialog = false)) }
            ) {
                onStateChange(fromRecord(it))
            }
    }


    LazyColumn(
        state = scrollState,
    ) {
        item {
            Row {
                Box(modifier = Modifier.weight(1f))
                {
                    LotNumField(
                        state.lotNum, state.lotNumIsValid,
                        onChange = {
                            onStateChange(
                                state.copy(
                                    lotNum = it,
                                    lotNumIsValid = it.isNotBlank()
                                )
                            )
                        },
                        scope, scrollState,
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    DrumNumberField(
                        state.drumNum, state.drumNumberIsValid,
                        onChange = {
                            onStateChange(
                                state.copy(drumNum = it, drumNumberIsValid = it.isNotBlank())
                            )
                        },
                        scope, scrollState
                    )
                }
            }
        }
        item {
            QtyField(state.qty, state.qtyIsValid, onChange = {
                if (scrollState.firstVisibleItemIndex != 1) {
                    scope.launch {
                        scrollState.scrollToItem(1)
                    }
                }
                onStateChange(
                    state.copy(qty = it, qtyIsValid = it.isNotBlank())
                )
            }, scope, scrollState)
        }
        item {
            NoteField(
                state.note,
                onChange = {
                    if (scrollState.firstVisibleItemIndex != 2) {
                        scope.launch {
                            scrollState.scrollToItem(2)
                        }
                    }
                    onStateChange(
                        state.copy(note = it,)
                    )
                },
                scope,
                scrollState
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                SaveButton(state, onStateChange = onStateChange) {
                    onSave(state.buildRecord(item, recordMeta))
                    onStateChange(state.clear())
                }
                Spacer(modifier = Modifier.width(24.dp))
                UpdateButton(validate = {
                    state.validate().also(onStateChange).isValid
                }) { onStateChange(state.copy(openUpdateDialog = true)) }
                Spacer(modifier = Modifier.width(24.dp))

                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.fill_from_old_record)) },
                    onClick = { onStateChange(state.copy(openSelectOldRecordDialog = true)) })
            }
        }


    }
}

@Composable
private fun UpdateButton(validate: () -> Boolean, onClick: () -> Unit) {

    ExtendedFloatingActionButton(text = {
        Text(text = stringResource(R.string.update))

    }, onClick = {
        if (validate()) {
            onClick()
        }

    })
}

@Composable
private fun SaveButton(
    state: RecordFormState,
    onStateChange: (RecordFormState) -> Unit,
    onSave: () -> Unit
) {
    val focusManger = LocalFocusManager.current
    ExtendedFloatingActionButton(text = { Text(stringResource(R.string.save)) },
        onClick = {
            if (state.validate().also(onStateChange).isValid) {
                onSave()
                focusManger.clearFocus()
            }

        })

}

@Composable
private fun NoteField(
    note: String,
    onChange: (String) -> Unit,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    StringField(
        value = note,
        onChange = onChange,
        isValidInput = true,
        padding = 4.dp,
        title = stringResource(R.string.note),
        isEnabled = true, modifier = Modifier
            .onFocusChanged {
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
    qty: String,
    qtyIsValid: Boolean,
    onChange: (String) -> Unit,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    StringField(
        value = qty,
        onChange = onChange,
        isValidInput = qtyIsValid,
        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Number),
        title = stringResource(R.string.qty), padding = 4.dp,
        isEnabled = true, modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) {
                    scope.launch {
                        scrollState.scrollToItem(1)
                    }
                }
            }
    )
}

@Composable
private fun DrumNumberField(
    drumNumber: String,
    drumNumberIsValid: Boolean,
    onChange: (String) -> Unit,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    StringField(
        value = drumNumber,
        onChange = onChange,
        isValidInput = drumNumberIsValid,
        title = stringResource(R.string.drum_number), padding = 4.dp,
        isEnabled = true, modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            }
    )
}

@Composable
private fun LotNumField(
    lotnum: String,
    lotnumIsValid: Boolean,
    onChange: (String) -> Unit,
    scope: CoroutineScope,
    scrollState: LazyListState
) {
    StringField(

        value = lotnum,
        onChange = onChange, padding = 4.dp,
        isValidInput = lotnumIsValid,
        title = stringResource(R.string.lot_number),
        isEnabled = true, modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            }
    )
}