package com.example.hospitalapp.classes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hospitalapp.R
import com.example.hospitalapp.ui.theme.HospitalAppTheme



data class Nurse(val id: Int, val name: String, val username: String,val profilePic : String)


class CreateNurses {
    val nurses = listOf(
        Nurse(1, "Nil Arroyo", "ni95_ar","perfil1"),
        Nurse(2, "Gerard Bartalot", "GeryBar","perfil2"),
        Nurse(3, "Joan Albert Chias", "Jachias","perfil3"),
        Nurse(4, "Sergio Arenas", "sejuma","perfil4"),
    )
}

@Composable
fun NurseApp(viewModel: CreateNurses, onBackPressed: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {        
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 40.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Button(onClick = onBackPressed) {
                    Text(text = "Back")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            NurseList(nurses = viewModel.nurses)
        }
    }
}

@Composable
fun NurseList(nurses: List<Nurse>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(nurses) { nurse ->
            NurseCard(nurse)
        }
    }
}

@Composable
fun NurseCard(nurse: Nurse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            GreetingImage(profilePic = nurse.profilePic)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ID: ${nurse.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: ${nurse.name}", fontSize = 14.sp)
                Text(
                    text = "Username: ${nurse.username}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )

            }
        }
    }
}
@Composable
fun GreetingImage(profilePic : String){
    val profileImageMap = mapOf(
        "perfil1" to R.drawable.perfil1,
        "perfil2" to R.drawable.perfil2,
        "perfil3" to R.drawable.perfil3,
        "perfil4" to R.drawable.perfil4
    )
    val imageResource = profileImageMap[profilePic] ?: R.drawable.default_pictures
    val image = painterResource(imageResource)
    Image(
        painter = image,
        contentDescription = null,
        modifier = Modifier.size(64.dp).padding(8.dp),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun NurseAppPreview() {
    HospitalAppTheme {
        NurseApp(viewModel = CreateNurses(), onBackPressed = {})
    }
}
