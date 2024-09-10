import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun Motivation(modifier: Modifier = Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    var quotes by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }

    // LaunchedEffect za dohvaćanje citata iz Firestore-a
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            val snapshot = db.collection("quotes").get().await()
            quotes = snapshot.documents.mapNotNull { it.getString("text") }
        } catch (e: Exception) {
            // Upravljanje pogreškom, npr. logiranje ili obavještavanje korisnika
            quotes = listOf("Error fetching quotes. Please try again.")
        }
    }

    // Provjerava postoji li barem jedan citat
    if (quotes.isNotEmpty()) {
        // Prikaz trenutne kartice s citatom
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuoteCard(quotes[currentIndex])

            Spacer(modifier = Modifier.height(16.dp))

            // Navigaciona dugmad
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dugme za prethodni citat
                Button(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    enabled = currentIndex > 0
                ) {
                    Text("Before")
                }

                // Dugme za sledeći citat
                Button(
                    onClick = { if (currentIndex < quotes.size - 1) currentIndex++ },
                    enabled = currentIndex < quotes.size - 1
                ) {
                    Text("Next")
                }
            }
        }
    } else {
        // Prikaz dok se podaci učitavaju ili ako nema citata
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading quotes...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun QuoteCard(quote: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Text(
                text = quote,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
