package com_2is.egypt.wipegadmin.ui.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import com_2is.egypt.wipegadmin.domain.gateways.PreferenceGateway
import com_2is.egypt.wipegadmin.ui.features.navigateToAddNewMaterial
import com_2is.egypt.wipegadmin.ui.features.navigateToAddNewRecord
import com_2is.egypt.wipegadmin.ui.features.navigateToLogin
import com_2is.egypt.wipegadmin.ui.sub_features.dialogs.*
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@Composable
fun HomeScreen(navHostController: NavHostController, homeViewModel: HomeViewModel) {
    val state = homeViewModel.state.collectAsState()
    if (state.value is HomeScreenState.Loading) {
        ProgressDialog()
    }
    when (state.value) {
        HomeScreenState.NavigateToAddRecord -> navHostController
            .navigateToAddNewRecord()
            .also { homeViewModel.eventHandled() }

        HomeScreenState.ErrorNoMetaData -> ShowErrorDialog(error = Exception("No Meta Data")) {
            homeViewModel.eventHandled()
        }
        HomeScreenState.ErrorRecordsUploaded -> RecordsUploadedErrorDialog {
            homeViewModel.eventHandled()
        }
        else -> {

            Scaffold(topBar = {
                TopAppBar(
                    title = { Text("Home") }, modifier = Modifier.statusBarsPadding(),
                    actions = { ControllerFAP(navHostController) })
            }) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    Button(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                        onClick = { homeViewModel.onNavigateToAddRecordClicked() }) {
                        Text(text = "Add WO Record")
                    }
                }

            }

        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferenceGateway: Lazy<PreferenceGateway>
) : ViewModel() {
    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.NormalState)
    val state: StateFlow<HomeScreenState> = _state
    fun onNavigateToAddRecordClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.run {
                value = HomeScreenState.Loading
                delay(500)
                value = preferenceGateway.get()
                    .run {
                        when {
                            addRecordLock -> HomeScreenState.ErrorRecordsUploaded
                            recordMeta != null -> HomeScreenState.NavigateToAddRecord
                            else -> null
                        }
                    }
                    ?: HomeScreenState.ErrorNoMetaData
            }

        }
    }


    fun eventHandled() {
        _state.value = HomeScreenState.NormalState
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object NavigateToAddRecord : HomeScreenState()
    object ErrorNoMetaData : HomeScreenState()
    object ErrorRecordsUploaded : HomeScreenState()
    object NormalState : HomeScreenState()
}

@Composable
private fun ControllerFAP(navController: NavHostController) {
    ExtendedFloatingActionButton(
        onClick = { navController.navigateToLogin() },
        icon = { Icon(imageVector = Icons.Rounded.Settings, null) },
        text = { Text(text = "Admin Controller") }
    )
}
