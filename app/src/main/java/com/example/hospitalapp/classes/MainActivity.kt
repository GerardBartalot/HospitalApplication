package com.example.hospitalapp.classes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hospitalapp.ui.theme.HospitalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HospitalAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "register") {
            composable("register") {
                RegisterScreen(
                    onBackPressed = {},
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable("getAll") {
                NurseApp(
                    remoteViewModel = RemoteViewModel(),
                    onBackPressed = { navController.popBackStack() }
                )
            }
            /*composable("findByName") {
                SearchScreen(
                    createNurses = RemoteViewModel(),
                    onBackPressed = { navController.popBackStack() }
                )
            }*/
            composable("search") {
                SearchScreen(navController = navController)
            }
            /*composable("profile") {
                ProfileScreen(
                    createNurses = RemoteViewModel(),
                    onBackPressed = { navController.popBackStack() })
            }*/
        }
    }
}