package com.example.hospitalapp.classes

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<Nurse>) : RemoteMessageUiState
    object Loading : RemoteMessageUiState
    object Error : RemoteMessageUiState

}

interface RemoteNurseInterface {
    @GET("/nurse/index")
    suspend fun getAllNurses(): List<Nurse>
}

class RemoteViewModel:ViewModel() {

 //   private val _remoteMessageUiState = MutableStateFlow<RemoteMessageUiState>(RemoteMessageUiState.Loading)
 //   val remoteMessageUiState: StateFlow<RemoteMessageUiState> = _remoteMessageUiState
    var remoteMessageUiState:RemoteMessageUiState by mutableStateOf(RemoteMessageUiState.Loading)
    private set

    fun getAllNurses() {
        viewModelScope.launch {
            remoteMessageUiState = RemoteMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Iniciando conexión Retrofit con base URL: http://10.0.2.2:8080")
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteNurseInterface::class.java)
                val response = endpoint.getAllNurses()
                remoteMessageUiState = RemoteMessageUiState.Success(response)
                Log.d("RemoteViewModel", "Datos recibidos: $response")
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error en la conexión o procesamiento: ${e.message}", e)
                remoteMessageUiState = RemoteMessageUiState.Error
            }
        }
    }
}
