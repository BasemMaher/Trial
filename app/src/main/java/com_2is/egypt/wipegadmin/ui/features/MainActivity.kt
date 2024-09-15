package com_2is.egypt.wipegadmin.ui.features

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com_2is.egypt.wipegadmin.ui.features.controller.ControllerScreen
import com_2is.egypt.wipegadmin.ui.features.controller.viewmodel.ControllerViewModel
import com_2is.egypt.wipegadmin.ui.features.login.LoginScreen
import com_2is.egypt.wipegadmin.ui.theme.WipEgAdminTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.insets.ProvideWindowInsets
import com_2is.egypt.wipegadmin.domain.dummy.fromJson
import com_2is.egypt.wipegadmin.domain.dummy.loadJSONFromAsset
import com_2is.egypt.wipegadmin.domain.gateways.ItemsDao
import com_2is.egypt.wipegadmin.domain.gateways.RecordsDao
import com_2is.egypt.wipegadmin.domain.gateways.ServerMaterialDao
import com_2is.egypt.wipegadmin.entites.*
import com_2is.egypt.wipegadmin.ui.features.add_material.AddMaterialScreen
import com_2is.egypt.wipegadmin.ui.features.add_material.viewmodel.AddMaterialViewModel
import com_2is.egypt.wipegadmin.ui.features.add_record.AddRecordScreen
import com_2is.egypt.wipegadmin.ui.features.add_record.viewmodel.AddRecordViewModel
import com_2is.egypt.wipegadmin.ui.features.admin_upload.AdminUploadScreen
import com_2is.egypt.wipegadmin.ui.features.home.HomeScreen
import com_2is.egypt.wipegadmin.ui.features.home.HomeViewModel
import com_2is.egypt.wipegadmin.ui.features.materials.viewModel.MaterialsViewModel
import com_2is.egypt.wipegadmin.ui.features.meta_data.viewmodel.MetaDataViewModel
import com_2is.egypt.wipegadmin.ui.features.records.viewmodel.RecordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var itemsDao: ItemsDao

    @Inject
    lateinit var recordsDao: RecordsDao

    @Inject
    lateinit var serverMaterialDao: ServerMaterialDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        loadItemsResponseAndSaveItToDatabase()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WipEgAdminTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    ScreensNavigator()

                }
            }

        }
    }
}

private fun MainActivity.loadItemsResponseAndSaveItToDatabase() {
    lifecycleScope.launch(Dispatchers.IO) {
        recordsDao.run {
            if (recordsCountFlow().firstOrNull() == 0)
                Record(
                    0,
                    "version test",
                    "area test",
                    "plant test",
                    "year test",
                    "month test",
                    "head note test",
                    "work order test",
                    "jop order test",
                    "item code test",
                    "class code test",
                    "lot number test",
                    "drum number test",
                    "qty test",
                    "note test",
                    UploadState.Uploaded("Done")
                ).let {
                    insertRecord(it)
                    insertRecord(it.copy(state = UploadState.ErrorWithUpload("error with upload")))
                    insertRecord(it.copy(state = UploadState.NotUploaded))
                    insertRecord(it.copy(state = UploadState.Uploading))
                }
        }
        if (itemsDao.getSyncedCount() == 0) {
            loadJSONFromAsset("service.json")
                .also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@loadItemsResponseAndSaveItToDatabase,
                            "start loading file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .await()?.fromJson<ItemsResponse>()
                ?.items
                ?.toLocaleItems()

                ?.sortedBy {
                    val s = it.workOrder.split("/")
                    if (s.size != 3) s.first() else s[1]
                }
                ?.reversed()
                ?.let {
                    itemsDao.insertAll(it)
                }?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@loadItemsResponseAndSaveItToDatabase,
                            "Done",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
        if (serverMaterialDao.getCount() == 0) {
            loadJSONFromAsset("materials.json")
                .also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@loadItemsResponseAndSaveItToDatabase,
                            "start loading materials file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .await()?.fromJson<MaterialsResponse>()
                ?.items?.toMutableList()?.apply {
                    repeat(50000) {
                        Random().nextInt(size)
                            .let(::get)
                            .copy(rmCode = randomRmCode())
                            .let(::add)
                    }
                }?.also { serverMaterialDao.insertAll(it) }
                ?.also {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@loadItemsResponseAndSaveItToDatabase,
                            "${it.size} materials added successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}

fun randomRmCode(): String {
    val random = Random()
    var i = 0
    return generateSequence {
        if (i == 5) null else random.nextInt(24).plus(65).toChar().also { i++; }
    }.toList().toCharArray().concatToString()

}


@Composable
private fun ScreensNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Home.route) {
        composable(Screens.Login.route) {
            LoginScreen(navController)
        }
        composable(Screens.Home.route) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(navController, homeViewModel)
        }
        composable(Screens.AddRecord.route) {
            val addRecordViewModel = hiltViewModel<AddRecordViewModel>()

            AddRecordScreen(navHController = navController, addRecordViewModel)
        }
        composable(Screens.AddMaterial.route) {

            it.arguments = navController.previousBackStackEntry?.arguments
            val addMaterialViewModel = hiltViewModel<AddMaterialViewModel>(backStackEntry = it)
            WipEgAdminTheme(darkTheme = !isSystemInDarkTheme()) {

                AddMaterialScreen(addMaterialViewModel, navHController = navController)
            }
        }
        composable(Screens.Controller.route) {
            val controllerViewModel = hiltViewModel<ControllerViewModel>()
            val metaDataViewModel = hiltViewModel<MetaDataViewModel>()
            val materialsViewModel = hiltViewModel<MaterialsViewModel>()
            val recordsViewModel = hiltViewModel<RecordsViewModel>()
            ControllerScreen(
                navController,
                controllerViewModel = controllerViewModel,
                metaDataViewModel = metaDataViewModel,
                recordsViewModel = recordsViewModel,
                materialsViewModel = materialsViewModel
            )
        }
        composable(Screens.AdminUploadScreen.route) {
            val materialsViewModel = hiltViewModel<MaterialsViewModel>()
            val recordsViewModel = hiltViewModel<RecordsViewModel>()
            AdminUploadScreen(recordsViewModel, materialsViewModel)
        }
    }
}

