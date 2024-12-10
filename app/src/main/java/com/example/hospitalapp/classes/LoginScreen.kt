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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

val users = listOf(
    Pair("ni95_ar", "password1"),
    Pair("GeryBar", "password2"),
    Pair("Jachias", "password3"),
    Pair("sejuma", "password4"),
)

@Composable
fun LoginScreen(
    navController: NavController,
    onBackPressed: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var loginMessageColor by remember { mutableStateOf(Color.Transparent) }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 16.dp)
                .offset(y = 32.dp)
        ) {
            Text("Back")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 200.dp)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 32.dp)
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
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                if (users.any { it.first == username && it.second == password }) {
                    loginMessageColor = Color.Green
                    loginMessage = "Login successful"
                    navController.navigate("search")
                } else {
                    loginMessage = "Incorrect username or password"
                    loginMessageColor = Color.Red
                }
            }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(25.dp))
            if (loginMessage.isNotEmpty()) {
                Text(text = loginMessage, color = loginMessageColor)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()

    LoginScreen(
        navController = navController,
        onBackPressed = {}
    )
}
