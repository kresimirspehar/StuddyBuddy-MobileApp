package com.example.myapplication.pages

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.AuthViewModel
import com.example.myapplication.R
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.compose.ui.draw.clip
import coil.compose.rememberImagePainter


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // State za korisničko ime, email i sliku
    var username by remember { mutableStateOf("Username") }
    var email by remember { mutableStateOf("email@example.com") }
    var isEditing by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(username) }
    var newEmail by remember { mutableStateOf(email) }

    // State za profilnu sliku
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Profilna slika s gumbom za promjenu
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .clickable {
                        launcher.launch("image/*") // Pokreće odabir slike iz galerije
                    }
            ) {
                profileImageUri?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prikaz korisničkog imena
            if (isEditing) {
                TextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Prikaz emaila
            if (isEditing) {
                TextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = email,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gumb za uredi profil
            if (isEditing) {
                Button(onClick = {
                    // Spremi promjene
                    username = newUsername
                    email = newEmail
                    isEditing = false
                    // Ovdje bi dodao funkcionalnost za spremanje promjena u bazu podataka ili ViewModel
                }) {
                    Text(text = "Save")
                }
            } else {
                Button(onClick = {
                    isEditing = true
                }) {
                    Text(text = "Edit profile")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gumb za odjavu
            Button(onClick = {
                authViewModel.logout() // Primjer odjave
                navController.navigate("login") // Navigacija na login ekran
            }) {
                Text(text = "Sign out")
            }
        }
    }
}
