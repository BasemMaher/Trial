package com_2is.egypt.wipegadmin.ui.features.controller

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.*
import com_2is.egypt.wipegadmin.ui.features.materials.MaterialsScreen
import com_2is.egypt.wipegadmin.ui.features.materials.viewModel.MaterialsViewModel
import com_2is.egypt.wipegadmin.ui.features.meta_data.MetaDataScreen
import com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel.MetaDataViewModel
import com_2is.egypt.wipegadmin.ui.features.records.RecordsScreen
import com_2is.egypt.wipegadmin.ui.features.records.viewmodel.RecordsViewModel
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import com_2is.egypt.wipegadmin.ui.sub_features.composables.PasswordField
import com_2is.egypt.wipegadmin.ui.sub_features.composables.ShowOrHidePasswordIcon
import com_2is.egypt.wipegadmin.ui.sub_features.composables.StringField
import com_2is.egypt.wipegadmin.ui.sub_features.composables.URLField

private const val TAG = "ControllerScreen"

@Composable
fun ControllerScreen(
    navController: NavHostController,
    controllerViewModel: ControllerViewModel,
    metaDataViewModel: MetaDataViewModel,
    recordsViewModel: RecordsViewModel,
    materialsViewModel: MaterialsViewModel
) {
    val state by controllerViewModel.state.collectAsState()
    state.apply {
        fetchResult?.let { HandleResult(it, controllerViewModel) }
        if (loading) ProgressDialog()
    }
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
        topBar = { ControllerBar(navController) },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomNavigation {
                ControllerScreens.values()
                    .forEach {
                        BottomNavItem(it, state.selectedScreen == it) {
                            ControllerEvent.ChangeSelectedScreen(it)
                                .let(controllerViewModel::addEvent)
                        }
                    }
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            when (state.selectedScreen) {
                ControllerScreens.Controller -> AdminControllerScreen(state, controllerViewModel)
                ControllerScreens.MetaData -> MetaDataScreen(metaDataViewModel = metaDataViewModel)
                ControllerScreens.Records -> RecordsScreen(viewModel = recordsViewModel)
                ControllerScreens.Materials -> MaterialsScreen(viewModel = materialsViewModel)
            }
        }

    }
}

@Composable
private fun AdminControllerScreen(
    state: ControllerState,
    controllerViewModel: ControllerViewModel
) {
    LazyColumn(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        item { AuthSection(state, controllerViewModel) }
        item { LoginSection(state, controllerViewModel) }
        item { ItemsSection(state, controllerViewModel) }
        item { UploadSection(state, controllerViewModel) }
        item { DeleteSection(state, controllerViewModel) }
        item {
            Box(modifier = Modifier.imePadding()) {
                FetchSection(state, controllerViewModel)
            }
        }

    }
}


@Composable
fun RowScope.BottomNavItem(
    screen: ControllerScreens,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    BottomNavigationItem(
        selected = isSelected,
        selectedContentColor = Color.White,
        unselectedContentColor = MaterialTheme.colors.secondary,
        onClick = onSelected,
        modifier = Modifier.background(
            if (isSelected)
                MaterialTheme.colors.secondary
            else
                MaterialTheme.colors.primary
        ),
        label = { Text(screen.label) },
        icon = { Icon(screen.Icon, null) })
}

@Composable
fun FetchSection(
    state: ControllerState,
    viewModel: ControllerViewModel
) {
    if (state.selectedSection == ControllerSection.Fetch)
        ControllerFABs(state, viewModel)
    else
        SectionCard(viewModel = viewModel, isValid = true, section = ControllerSection.Fetch)
}

@Composable
private fun ControllerFABs(
    state: ControllerState,
    viewModel: ControllerViewModel
) {
    val focus = LocalFocusManager.current
    if (!state.loading)
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            FetchButton(
                text = stringResource(R.string.fetch_token),
                onClick = {
                    focus.clearFocus()
                    viewModel.addEvent(ControllerEvent.FetchToken)
                })
            Spacer(modifier = Modifier.height(16.dp))
            FetchButton(
                text = stringResource(id = R.string.fetch_data),
                onClick = {
                    focus.clearFocus()
                    val event = ControllerEvent.FetchItems
                    viewModel.addEvent(event)
                })
            Spacer(modifier = Modifier.height(16.dp))
            FetchButton(
                text = "Fetch Materials",
                onClick = {
                    focus.clearFocus()
                    val event = ControllerEvent.FetchMaterials
                    viewModel.addEvent(event)
                }
            )

        }
}


@Composable
private fun UploadSection(state: ControllerState, viewModel: ControllerViewModel) =
    if (state.selectedSection == ControllerSection.Upload)
        Column() {
            UploadRecordsUrlField(state, viewModel)
            UploadMaterialsUrlField(state, viewModel)
        }
    else
        SectionCard(
            viewModel,
            state.isUploadItemURLIsValid && state.isUploadMaterialURLIsValid,
            ControllerSection.Upload
        )

@Composable
private fun DeleteSection(
    state: ControllerState,
    viewModel: ControllerViewModel
) {
    var openDeleteRecordDialog by remember {
        mutableStateOf(false)
    }
    var deleteEvent: ControllerEvent? by remember {
        mutableStateOf(null)
    }
    if (openDeleteRecordDialog) {
        val deleteMessage = if (deleteEvent is ControllerEvent.DeleteRecords)
            stringResource(R.string.delete_records_alert_message)
        else
            "Are You Sure You Want to delete All Materials"
        DeleteAlertDialog(
            deleteMessage = deleteMessage,
            dismiss = { openDeleteRecordDialog = false },
            onDeleteClicked = {
                deleteEvent?.let(viewModel::addEvent)
            })
    }

    if (state.selectedSection == ControllerSection.Delete)
        Column {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.error,
                    contentColor = Color.White
                ),
                onClick = {
                    deleteEvent = ControllerEvent.DeleteRecords
                    openDeleteRecordDialog = true
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = stringResource(R.string.delete_all_records))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.error,
                    contentColor = Color.White
                ),
                onClick = {
                    deleteEvent = ControllerEvent.DeleteMaterials
                    openDeleteRecordDialog = true
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = stringResource(R.string.delete_all_materials))
            }
        }
    else
        SectionCard(
            viewModel,
            true,
            ControllerSection.Delete
        )


}

@Composable
private fun ItemsSection(state: ControllerState, viewModel: ControllerViewModel) =
    if (state.selectedSection == ControllerSection.Items)
        Column() {
            ItemsUrlField(state, viewModel)
            MaterialsUrlField(state, viewModel)
        }
    else
        SectionCard(
            viewModel,
            state.isItemsURLValid && state.isMaterialsURLValid,
            ControllerSection.Items
        )

@Composable
private fun AuthSection(state: ControllerState, viewModel: ControllerViewModel) =
    if (state.selectedSection == ControllerSection.Auth)
        AuthURLField(state, viewModel)
    else
        SectionCard(viewModel, state.isAuthURLValid, ControllerSection.Auth)

@Composable
private fun LoginSection(state: ControllerState, viewModel: ControllerViewModel) =
    if (state.selectedSection == ControllerSection.Login)
        Column {
            UserNameField(state, viewModel)
            GrantTypeField(state, viewModel)
            ControllerPasswordField(state, viewModel)
        }
    else
        SectionCard(viewModel, state.isAuthValid, ControllerSection.Login)


@Composable
private fun SectionCard(
    viewModel: ControllerViewModel,
    isValid: Boolean,
    section: ControllerSection
) = Card(
    modifier = Modifier
        .padding(8.dp),
    border = BorderStroke(
        1.dp, if (isValid) Color.White else MaterialTheme.colors.error
    )
) {
    TextButton(onClick = {
        viewModel.addEvent(
            ControllerEvent.ChangeSelectedSection(section)
        )
    }) {
        Text(text = section.label, modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ArrowDropDown, null)
    }

}

@Composable
fun HandleResult(fetchDataResult: ServerResult, viewModel: ControllerViewModel) {
    val dismiss = { viewModel.addEvent(ControllerEvent.ResultHandled) }
    when (fetchDataResult) {
        is ServerResult.Error -> {
            Log.e(TAG, "exception ", fetchDataResult.error)
            ShowErrorDialog(
                fetchDataResult.error,
                dismiss = dismiss
            )
        }

        is ServerResult.ItemsFetched -> ShowSuccessDialog(
            "${fetchDataResult.itemsCount} item inserted",
            dismiss = dismiss
        )
        is ServerResult.TokenFetched -> ShowSuccessDialog(
            fetchDataResult.expires,
            dismiss = dismiss
        )
        is ServerResult.RecordsDeleted -> ShowSuccessDialog(
            "${fetchDataResult.itemsCount} record deleted",
            dismiss = dismiss
        )
    }
}

@Composable
private fun ItemsUrlField(
    state: ControllerState,
    viewModel: ControllerViewModel
) {
    URLField(
        isEnabled = !state.loading,
        url = state.itemsURL,
        onChange = {
            viewModel.addEvent(ControllerEvent.ChangeItemsURL(it))
        },
        isURLValid = state.isItemsURLValid,
        title = stringResource(R.string.items_url)
    )
}

@Composable
private fun MaterialsUrlField(
    state: ControllerState,
    viewModel: ControllerViewModel
) {
    URLField(
        isEnabled = !state.loading,
        url = state.materialsURL,
        onChange = {
            viewModel.addEvent(ControllerEvent.ChangeMaterialsURL(it))
        },
        isURLValid = state.isMaterialsURLValid,
        title = "Materials URL"
    )
}

@Composable
private fun UploadRecordsUrlField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = URLField(
    isEnabled = !state.loading,
    url = state.uploadRecordURL,
    onChange = {
        viewModel.addEvent(ControllerEvent.ChangeUploadRecordURL(it))
    },
    isURLValid = state.isUploadItemURLIsValid,
    title = stringResource(R.string.upload_record_url)
)

@Composable
private fun UploadMaterialsUrlField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = URLField(
    isEnabled = !state.loading,
    url = state.uploadMaterialURL,
    onChange = {
        viewModel.addEvent(ControllerEvent.ChangeUploadMaterialURL(it))
    },
    isURLValid = state.isUploadMaterialURLIsValid,
    title = "Upload Material URL"
)

@Composable
private fun AuthURLField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = URLField(
    isEnabled = !state.loading,
    url = state.authURL,
    onChange = {
        viewModel.addEvent(ControllerEvent.ChangeAuthURL(it))
    },
    isURLValid = state.isAuthURLValid,
    title = stringResource(R.string.auth_url),
)

@Composable
private fun UserNameField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = StringField(
    isEnabled = !state.loading,
    padding = 8.dp,
    value = state.userName, onChange = {
        viewModel.addEvent(ControllerEvent.ChangeUserName(it))
    }, isValidInput = state.isUserNameValid, title = stringResource(
        R.string.user_name
    ), modifier = Modifier.focusTarget()
)

@Composable
private fun GrantTypeField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = StringField(
    isEnabled = !state.loading, padding = 8.dp,
    value = state.grantType, onChange = {
        viewModel.addEvent(ControllerEvent.ChangeGrantType(it))
    }, isValidInput = state.isGrantTypeValid, title = stringResource(R.string.grant_type)
)

@Composable
private fun ControllerPasswordField(
    state: ControllerState,
    viewModel: ControllerViewModel
) = PasswordField(
    isEnabled = !state.loading,
    isPasswordVisible = state.passwordIsVisible,
    password = state.password,
    modifier = Modifier,
    onChange = {
        viewModel.addEvent(ControllerEvent.ChangePassword(password = it))
    },
    isPasswordValid = state.isPasswordValid
) {
    ShowOrHidePasswordIcon(passwordIsVisible = state.passwordIsVisible) {
        viewModel.addEvent(ControllerEvent.ShowOrHidePassword)
    }
}


@Composable
fun ControllerBar(navController: NavHostController) = TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    title = {
        Text(
            stringResource(R.string.controller),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }, navigationIcon = {
        IconButton(
            onClick = { navController.popBackStack() }) {
            Icon(Icons.Rounded.ArrowBack, null)
        }
    })


@Composable
fun FetchButton(onClick: () -> Unit, text: String) = Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    content = {
        Text(text)
    })

