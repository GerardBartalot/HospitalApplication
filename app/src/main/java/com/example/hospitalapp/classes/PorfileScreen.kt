package com.example.hospitalapp.classes

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    remoteViewModel: RemoteViewModel,
    onBackPressed: () -> Unit,
    nurseId: Int,
    onDelete: (Int) -> Unit,
    navController: NavController
) {
    val remoteMessageUiState = remoteViewModel.remoteMessageUiState
    var foundNurse by remember { mutableStateOf<Nurse?>(null) }
    var isSearchPerformed by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var updateMessage by remember { mutableStateOf("") }

    LaunchedEffect(remoteMessageUiState) {
        if (remoteMessageUiState is RemoteMessageUiState.Success && !isSearchPerformed) {
            foundNurse = remoteMessageUiState.remoteMessage.find { it.nurse_id == nurseId }
            isSearchPerformed = true
            foundNurse?.let {
                name = it.name ?: ""
                username = it.username ?: ""
                password = it.password ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 30.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Button(onClick = onBackPressed) {
                Text(text = "Back")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (remoteMessageUiState) {
            is RemoteMessageUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is RemoteMessageUiState.Error -> {
                Text(
                    text = "Error loading data.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is RemoteMessageUiState.Success -> {
                if (foundNurse != null) {
                    val nurse = foundNurse!!
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            val updatedNurse = Nurse(
                                nurse_id = nurseId,
                                name = name,
                                username = username,
                                password = password
                            )
                            remoteViewModel.updateNurse(
                                updatedNurse,
                                onSuccess = {
                                    Log.d("ProfileScreen", "Nurse actualizado con éxito")
                                    updateMessage = "Usuario actualizado con éxito"
                                },
                                onError = { errorMessage ->
                                    Log.e("ProfileScreen", "Error al actualizar el usuario: $errorMessage")
                                    updateMessage = "Error al actualizar: $errorMessage"
                                }
                            )
                        }) {
                            Text("Update")
                        }
                        Button(onClick = {
                            remoteViewModel.deleteNurse(
                                nurseId,
                                onSuccess = {
                                    Log.d("ProfileScreen", "Nurse eliminado con éxito")
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = false }
                                    }
                                },
                                onError = { errorMessage ->
                                    Log.e("ProfileScreen", "Error al eliminar el usuario: $errorMessage")
                                }
                            )
                        }) {
                            Text("Delete")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (updateMessage.isNotEmpty()) {
                        Text(
                            text = updateMessage,
                            color = if (updateMessage.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Nurse not found.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
