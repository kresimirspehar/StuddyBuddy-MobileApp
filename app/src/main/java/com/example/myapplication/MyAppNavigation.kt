package com.example.myapplication

import Progress
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.pages.HomePage
import com.example.myapplication.pages.LoginPage
import com.example.myapplication.pages.Motivation
import com.example.myapplication.pages.SignupPage
import androidx.navigation.NavHostController
import com.example.myapplication.pages.Profile


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navController: NavHostController // Promenjen tip u NavHostController
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("progress") {
            Progress(modifier, navController, authViewModel)
        }
        composable("motivation") {
            Motivation(modifier, navController, authViewModel)
        }
        composable("profile") {
            Profile(modifier, navController, authViewModel)
        }
    }
}


