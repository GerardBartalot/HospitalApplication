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
import retrofit2.http.PUT
import retrofit2.http.Path

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<Nurse>) : RemoteMessageUiState
    object Loading : RemoteMessageUiState
    object Error : RemoteMessageUiState
    data class ResponseMessage(val message: String)
}
sealed interface LoginMessageUiState {
    data class Success(val loginMessage: Nurse?) : LoginMessageUiState
    object Loading : LoginMessageUiState
    object Error : LoginMessageUiState


}
sealed interface RegisterMessageUiState {
    data class Success(val nurse: Nurse?) : RegisterMessageUiState
    object Loading : RegisterMessageUiState
    object Error : RegisterMessageUiState
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
    ): Response<Nurse>

    @POST("/nurse/create")
    suspend fun registerUser(@Body nurse: Nurse): Response<Nurse>

    @PUT("/nurse/delete/{id}")
    suspend fun deleteNurseById(@Path("id") id: Int): Response<Map<String, String>>

    @PUT("/nurse/update/{id}")
    suspend fun  updateNurse(@Path("id") id: Int, @Body updatedNurse: Nurse ): Response<RemoteMessageUiState.ResponseMessage>

}

// ViewModel
class RemoteViewModel : ViewModel() {

    // State variables for Compose
    var remoteMessageUiState: RemoteMessageUiState by mutableStateOf(RemoteMessageUiState.Loading)
        private set
    var loginMessageUiState: LoginMessageUiState by mutableStateOf(LoginMessageUiState.Loading)
        private set
    private val _registerMessageUiState = MutableStateFlow<RegisterMessageUiState>(RegisterMessageUiState.Loading)
    val registerMessageUiState: StateFlow<RegisterMessageUiState> = _registerMessageUiState

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

    fun registerUser(nurse: Nurse, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _registerMessageUiState.value = RegisterMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Intentando registrar usuario: ${nurse.username}")

                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteNurseInterface::class.java)
                val response = endpoint.registerUser(nurse)

                Log.d("RemoteViewModel", "Código de respuesta: ${response.code()}")
                Log.d("RemoteViewModel", "Mensaje de respuesta: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _registerMessageUiState.value = RegisterMessageUiState.Success(responseBody)
                        Log.d("RemoteViewModel", "Registro exitoso para usuario: ${nurse.username}")
                        onResult("Registro exitoso")
                    } else {
                        _registerMessageUiState.value = RegisterMessageUiState.Error
                        Log.e("RemoteViewModel", "Registro fallido: respuesta vacía")
                        onResult("Error: Datos no recibidos")
                    }
                } else {
                    _registerMessageUiState.value = RegisterMessageUiState.Error
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("RemoteViewModel", "Error en registro: $errorMessage")
                    onResult("Error: $errorMessage")
                }
            } catch (e: Exception) {
                _registerMessageUiState.value = RegisterMessageUiState.Error
                Log.e("RemoteViewModel", "Excepción durante el registro: ${e.localizedMessage}", e)
                onResult("Error: Problema de conexión")
            }
        }
    }

    fun deleteNurse(
        id: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("RemoteViewModel", "Deleting nurse with id: $id")
                val response = endpoint.deleteNurseById(id)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.get("message") ?: "Operación completada sin mensaje"
                    onSuccess(message)
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error al eliminar: ${e.message}")
                Log.e("RemoteViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    fun updateNurse(
        updatedNurse: Nurse,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("RemoteViewModel", "Updating nurse with id: ${updatedNurse.nurse_id}")
                val response = endpoint.updateNurse(updatedNurse.nurse_id, updatedNurse)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Operación completada sin mensaje"
                    onSuccess(message)
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error al actualizar: ${e.message}")
                Log.e("RemoteViewModel", "Exception: ${e.message}", e)
            }
        }
    }




}

