package com.example.hospitalapp.classes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

data class User(
    val username: String,
    val password: String,
)

class UserViewModel : ViewModel() {
    private val _users = mutableStateListOf(
        User("ni95_ar", "password1"),
        User("GeryBar", "password2"),
        User("Jachias", "password3"),
        User("sejuma", "password4"),
    )
    val users: List<User> get() = _users

    fun registerUser(user: User): String {
        return if (_users.any { it.username == user.username }) {
            "User already registered"
        } else {
            _users.add(user)
            "User successfully registered"
        }
    }
}

@Composable
fun RegisterScreen(
    onBackPressed: () -> Unit,
    onNavigateToLogin: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Transparent) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Hospital Application",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 50.dp)
            )

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Surname") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                if (username.isNotBlank() && password.isNotBlank() &&
                    name.isNotBlank() && surname.isNotBlank() &&
                    address.isNotBlank() && phone.isNotBlank()
                ) {
                    if (userViewModel.users.any { it.username == username }) {
                        message = "User already registered"
                        messageColor = Color.Red
                    } else if (userViewModel.users.any { it.password == password }) {
                        message = "The password is already in use"
                        messageColor = Color.Red
                    } else {
                        val newUser = User(username, password)
                        userViewModel.registerUser(newUser)
                        message = "User successfully registered"
                        messageColor = Color.Green
                    }
                } else {
                    message = "Please, complete all fields"
                    messageColor = Color.Red
                }
            }) {
                Text("Register")
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = messageColor,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToLogin
                ) {
                    Text("Login", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onBackPressed = {},
        onNavigateToLogin = {}
    )
}
