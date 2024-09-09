package com.example.myapplication.pages

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.AuthState
import com.example.myapplication.AuthViewModel
import com.example.myapplication.NotificationReceiver
import com.example.myapplication.R
import com.example.myapplication.Todo
import com.example.myapplication.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextDecoration


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val todoViewModel: TodoViewModel = viewModel() // Koristi ViewModelProvider za instanciranje ViewModel-a

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Sign out button positioned at the top right
        TextButton(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Sign out")
        }

        // Column for the rest of the content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp) // Adjust padding to ensure content is not obscured by the button
        ) {
            // Todo list positioned in the center
            TodoListPage(viewModel = todoViewModel) // Prosleđivanje instance TodoViewModel
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.observeAsState()
    var inputText by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<Todo?>(null) } // Stanje za trenutno uređivanje
    var showError by remember { mutableStateOf(false) } // Stanje za prikaz greške


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    showError = false // Sakrij grešku kad korisnik menja unos
                },
                isError = showError, // Postavi grešku ako je potrebno
                placeholder = { Text("Enter a todo item") }
            )
            Button(
                onClick = {
                    if (inputText.trim().isNotEmpty()) {
                        viewModel.addTodo(inputText)
                        inputText = ""
                        showError = false
                    } else {
                        showError = true // Prikaz greške ako je unos prazan
                    }
                }
            ) {
                Text(text = "Add")
            }
        }

        if (showError) {
            Text(
                text = "Todo item cannot be empty",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        todoList?.let {
            LazyColumn(
                content = {
                    itemsIndexed(it) { index: Int, item: Todo ->
                        TodoItem(
                            item = item,
                            isEditing = editingItem == item,
                            onEdit = { editingItem = item },
                            onSave = { newText ->
                                viewModel.updateTodo(item.copy(title = newText)) // Ažuriraj naziv stavke
                                editingItem = null // Zatvori uređivanje
                            },
                            onCancel = {
                                editingItem = null // Zatvori uređivanje bez spremanja
                            },
                            onDelete = {
                                viewModel.deleteTodo(item.id)
                            }
                        )
                    }
                }
            )
        } ?: Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "No items yet",
            fontSize = 16.sp
        )
    }
}

@Composable
fun TodoItem(
    item: Todo,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    var editText by remember { mutableStateOf(item.title) }
    var showDialog by remember { mutableStateOf(false) }
    var isNotificationActive by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) } // Stanje za checkbox

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isChecked) Color.Gray else MaterialTheme.colorScheme.primary
            ) // Boja se menja kada je checkbox označen
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it } // Oznaci ili poništi checkbox
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SimpleDateFormat("HH:mm:aa, dd/MM", Locale.ENGLISH).format(item.createdAt),
                fontSize = 12.sp,
                color = if (isChecked) Color.DarkGray else Color.LightGray
            )
            if (isEditing && !isChecked) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    IconButton(onClick = { onSave(editText) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_check_24),
                            contentDescription = "Save"
                        )
                    }
                    IconButton(onClick = onCancel) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cancel_24),
                            contentDescription = "Cancel"
                        )
                    }
                }
            } else {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    color = if (isChecked) Color.Gray else Color.White,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier
                        .clickable(enabled = !isChecked) { onEdit() } // Onemogući klik ako je checkbox označen
                )
            }
        }

        IconButton(onClick = { showDialog = true }, enabled = !isChecked) { // Onemogući dugme za notifikacije
            Icon(
                painter = painterResource(id = R.drawable.baseline_notifications_24),
                contentDescription = "Notify"
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Odaberite vrijeme") },
                text = {
                    val context = LocalContext.current

                    Column {
                        TextButton(onClick = {
                            setAlarm(context = context, item.title, 1)
                            isNotificationActive = true // Označi da je notifikacija aktivna
                            showDialog = false
                            Toast.makeText(context, "Notifikacija postavljena za 1 sat", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("1 sat")
                        }
                        TextButton(onClick = {
                            setAlarm(context = context, item.title, 12)
                            isNotificationActive = true
                            showDialog = false
                            Toast.makeText(context, "Notifikacija postavljena za 12 sati", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("12 sati")
                        }
                        TextButton(onClick = {
                            setAlarm(context = context, item.title, 24)
                            isNotificationActive = true
                            showDialog = false
                            Toast.makeText(context, "Notifikacija postavljena za 24 sata", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("24 sata")
                        }
                        // Prikaz "Poništi" samo ako je notifikacija aktivna
                        if (isNotificationActive) {
                            TextButton(onClick = {
                                cancelAlarm(context = context, item.title)
                                isNotificationActive = false // Oznaci da je notifikacija poništena
                                showDialog = false
                            }) {
                                Text("Poništi")
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        IconButton(onClick = onDelete, enabled = !isChecked) { // Onemogući dugme za brisanje
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete"
            )
        }
    }
}

fun setAlarm(context: Context, title: String, hours: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("notificationId", System.currentTimeMillis().toInt())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Vreme kada će se alarm okinuti (sada + zadani sati)
    val triggerTime = System.currentTimeMillis() + hours * 60 * 60 * 1000

    // Postavljanje alarma
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

fun cancelAlarm(context: Context, title: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title", title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Otkazivanje alarma
    alarmManager.cancel(pendingIntent)
    Toast.makeText(context, "Notifikacija poništena", Toast.LENGTH_SHORT).show()
}


