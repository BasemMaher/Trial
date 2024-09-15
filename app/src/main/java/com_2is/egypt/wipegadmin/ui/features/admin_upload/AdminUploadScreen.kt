package com_2is.egypt.wipegadmin.ui.features.admin_upload

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com_2is.egypt.wipegadmin.ui.features.controller.BottomNavItem
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerEvent
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerScreens
import com_2is.egypt.wipegadmin.ui.features.materials.MaterialsScreen
import com_2is.egypt.wipegadmin.ui.features.materials.viewModel.MaterialsViewModel
import com_2is.egypt.wipegadmin.ui.features.records.RecordsScreen
import com_2is.egypt.wipegadmin.ui.features.records.viewmodel.RecordsViewModel

@Composable
fun AdminUploadScreen(recordsViewModel: RecordsViewModel, materialsViewModel: MaterialsViewModel) {
    var selectedScreen by remember {
        mutableStateOf(ControllerScreens.Materials)
    }
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(title = { Text("Admin") })

        },
        bottomBar = {
            BottomAppBar {
                listOf(ControllerScreens.Materials, ControllerScreens.Records).forEach {
                    BottomNavItem(it, selectedScreen == it) {
                        selectedScreen = it
                    }
                }

            }
        }) {
        Box(modifier = Modifier.padding(it)) {
            when (selectedScreen) {
                ControllerScreens.Records -> RecordsScreen(viewModel = recordsViewModel)
                ControllerScreens.Materials -> MaterialsScreen(viewModel = materialsViewModel)
                else -> Unit
            }
        }

    }


}