package com.example.myapplication.pages

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.myapplication.AuthViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment


@Composable
fun Motivation(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    val quotes = listOf(
        "Success is the sum of small efforts, repeated day in and day out.",
        "The beautiful thing about learning is that no one can take it away from you." ,
        "Education is the most powerful weapon which you can use to change the world.",
        "Don’t watch the clock; do what it does. Keep going.",
        "Success doesn’t come from what you do occasionally, it comes from what you do consistently.",
        "Learning never exhausts the mind.",
        "The more you learn, the more you earn.",
        "Push yourself, because no one else is going to do it for you.",
        "Your only limit is your mind.",
        "Dream big, work hard, stay focused, and surround yourself with good people.",
        "Great things never come from comfort zones.",
        "The future belongs to those who believe in the beauty of their dreams.",
        "Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful.",
        "Strive for progress, not perfection.",
        "Don’t be afraid to fail. Be afraid not to try.",
        "Study while others are sleeping; work while others are loafing; prepare while others are playing; and dream while others are wishing.",
        "It’s not about how bad you want it. It’s about how hard you’re willing to work for it.",
        "Motivation is what gets you started. Habit is what keeps you going.",
        "Believe you can and you’re halfway there."
    )

    var currentIndex by remember { mutableStateOf(0) }

    // Prikaz trenutne kartice
    Column(
        modifier = Modifier
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
                onClick = {
                    if (currentIndex > 0) {
                        currentIndex--
                    }
                },
                enabled = currentIndex > 0 // Disable dugme na prvom citatu
            ) {
                Text("Prethodni")
            }

            // Dugme za sledeći citat
            Button(
                onClick = {
                    if (currentIndex < quotes.size - 1) {
                        currentIndex++
                    }
                },
                enabled = currentIndex < quotes.size - 1 // Disable dugme na poslednjem citatu
            ) {
                Text("Sljedeći")
            }
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

