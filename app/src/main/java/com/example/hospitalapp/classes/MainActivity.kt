package com.example.hospitalapp.classes

import android.os.Bundle
import android.util.Log
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
        val remoteViewModel = RemoteViewModel()
        setContent {
            HospitalAppTheme {
                AppNavigation(remoteViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(remoteViewModel: RemoteViewModel) {
    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "login") {
            composable("register") {
                RegisterScreen(
                    remoteViewModel = remoteViewModel,
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    remoteViewModel = remoteViewModel,
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToSearch = { nurseId ->
                        navController.navigate("search/$nurseId")
                    }
                )
            }
            composable("getAll") {
                NurseApp(
                    remoteViewModel = remoteViewModel,
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable("findByName") {
                SearchScreen(
                    remoteViewModel = remoteViewModel,
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable("search/{nurseId}") { backStackEntry ->
                val nurseId = backStackEntry.arguments?.getString("nurseId")?.toIntOrNull()
                if (nurseId != null) {
                    SearchScreen(navController = navController, nurse_id = nurseId)
                } else {
                    Log.e("AppNavigation", "Invalid nurseId passed to search screen")
                }
            }
            composable("profile/{nurseId}") { backStackEntry ->
                val nurseId = backStackEntry.arguments?.getString("nurseId")?.toIntOrNull()
                if (nurseId != null) {
                    ProfileScreen(
                        remoteViewModel = remoteViewModel,
                        onBackPressed = { navController.popBackStack() },
                        nurseId = nurseId,
                        onDelete = { id ->
                            remoteViewModel.deleteNurse(
                                id = id,
                                onSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = false }
                                    }
                                },
                                onError = { errorMessage ->
                                    Log.e("AppNavigation", "Error al eliminar el usuario: $errorMessage")
                                }
                            )
                        },
                        navController = navController
                    )
                } else {
                    Log.e("AppNavigation", "Invalid nurseId passed to profile screen")
                }
            }

        }
    }
}