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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(remoteViewModel: RemoteViewModel, onBackPressed: () -> Unit, nurseId: Int) {
    val remoteMessageUiState = remoteViewModel.remoteMessageUiState
    var foundNurse by remember { mutableStateOf<Nurse?>(null) }
    var isSearchPerformed by remember { mutableStateOf(false) }

    // Solo ejecutar la búsqueda cuando el estado de la UI haya cambiado
    LaunchedEffect(remoteMessageUiState) {
        if (remoteMessageUiState is RemoteMessageUiState.Success && !isSearchPerformed) {
            foundNurse = remoteMessageUiState.remoteMessage.find { it.nurse_id == nurseId }
            isSearchPerformed = true
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
                foundNurse?.let { nurse ->
                    TextField(
                        value = nurse.name,
                        onValueChange = {},
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = nurse.username,
                        onValueChange = {},
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = nurse.password,
                        onValueChange = {},
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { /* TODO: Implement update logic */ }) {
                            Text("Update")
                        }
                        Button(onClick = { /* TODO: Implement delete logic */ }) {
                            Text("Delete")
                        }
                    }
                } ?: run {
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

