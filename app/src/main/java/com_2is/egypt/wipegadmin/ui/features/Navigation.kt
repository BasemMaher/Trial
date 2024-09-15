package com_2is.egypt.wipegadmin.ui.features

import android.os.Bundle
import androidx.navigation.NavHostController

enum class Screens(val route: String) {
    Login("login"),
    Home("home"),
    Controller("controller"),
    AdminUploadScreen("admin_upload"),
    AddRecord("add_record"),
    AddMaterial("add_material"),
}

fun NavHostController.navigateToLogin() =
    navigate(Screens.Login.route)

fun NavHostController.navigateToController() =
    navigate(Screens.Controller.route) {
        popUpTo(Screens.Login.route) {
            inclusive = true
        }
    }
fun NavHostController.navigateToAdminUpload() =
    navigate(Screens.AdminUploadScreen.route) {
        popUpTo(Screens.Login.route) {
            inclusive = true
        }
    }

fun NavHostController.navigateToAddNewRecord() =
    navigate(Screens.AddRecord.route)

fun NavHostController.navigateToAddNewMaterial(workOrder: String) =
    apply {
        currentBackStackEntry?.arguments = Bundle().apply {
            putString("work_order", workOrder)
        }
        navigate(Screens.AddMaterial.route)
    }