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
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

// UI State Sealed Interfaces
sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<Nurse>) : RemoteMessageUiState
    object Loading : RemoteMessageUiState
    object Error : RemoteMessageUiState
}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: User?) : LoginMessageUiState
    object Loading : LoginMessageUiState
    object Error : LoginMessageUiState
}

// Retrofit Interfaces
interface RemoteNurseInterface {
    @GET("/nurse/index")
    suspend fun getAllNurses(): List<Nurse>

    @FormUrlEncoded
    @POST("/nurse/login")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    @POST("/nurse/create")
    suspend fun registerUser(@Body user: User): Response<User>
}

// ViewModel
class RemoteViewModel : ViewModel() {

    // State variables for Compose
    var remoteMessageUiState: RemoteMessageUiState by mutableStateOf(RemoteMessageUiState.Loading)
        private set
    var loginMessageUiState: LoginMessageUiState by mutableStateOf(LoginMessageUiState.Loading)
        private set

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val endpoint: RemoteNurseInterface by lazy {
        retrofit.create(RemoteNurseInterface::class.java)
    }

    fun getAllNurses() {
        viewModelScope.launch {
            remoteMessageUiState = RemoteMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Iniciando conexión Retrofit con base URL: http://10.0.2.2:8080")
                val response = endpoint.getAllNurses()
                remoteMessageUiState = RemoteMessageUiState.Success(response)
                Log.d("RemoteViewModel", "Datos recibidos: $response")
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error en la conexión o procesamiento: ${e.message}", e)
                remoteMessageUiState = RemoteMessageUiState.Error
            }
        }
    }

    fun loginUser(username: String, password: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            loginMessageUiState = LoginMessageUiState.Loading
            try {
                val response = endpoint.loginUser(username, password)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        loginMessageUiState = LoginMessageUiState.Success(responseBody)
                        onResult("Login exitoso")
                    } else {
                        loginMessageUiState = LoginMessageUiState.Error
                        onResult("Error: Datos no recibidos")
                    }
                } else {
                    loginMessageUiState = LoginMessageUiState.Error
                    onResult("Error: Credenciales incorrectas")
                }
            } catch (e: Exception) {
                loginMessageUiState = LoginMessageUiState.Error
                onResult("Error: Problema de conexión")
                Log.e("RemoteViewModel", "Error en login: ${e.message}", e)
            }
        }
    }

    fun registerUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.registerUser(user)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error en el registro: ${e.message}", e)
                onResult(false)
            }
        }
    }
}
