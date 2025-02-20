package com.example.flashcardquiz

import android.annotation.SuppressLint
import android.content.Context
import org.xmlpull.v1.XmlPullParser

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.flashcardquiz.ui.theme.FlashcardQuizTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardQuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(context = this)
                }
            }
        }
    }
}



// Flashcard class
data class Flashcard(val question: String, val answer: String)

//loading flashcards from XML
fun loadFlashcards(context: Context): List<Flashcard> {
    val flashcards = mutableListOf<Flashcard>()
    val parser = context.resources.getXml(R.xml.flashcards)

    var question = ""
    var answer = ""

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        when (parser.eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "question" -> question = parser.nextText()
                    "answer" -> answer = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "card") {
                    flashcards.add(Flashcard(question, answer))
                }
            }
        }
        parser.next()
    }
    return flashcards
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainScreen(context: Context) {
    //Loads flashcards
    val flashcards = remember { mutableStateOf(loadFlashcards(context)) }
    var flippedIndex = remember { mutableStateOf(-1) }

    LazyRow(modifier = Modifier.fillMaxSize()) {
        items(flashcards.value) { flashcard ->
            FlashcardItem(flashcard, flashcards.value.indexOf(flashcard), flippedIndex.value) { index ->
                flippedIndex.value = if (flippedIndex.value == index) -1 else index
            }
        }
    }

    //coroutine
    val coroutineScope = rememberCoroutineScope()
    // Coroutine
    coroutineScope.launch() {
        while (true) { // forever
            delay(15000) // shuffle every 15 seconds
            flashcards.value = flashcards.value.shuffled()
        }
    }
}


@Composable
fun FlashcardItem(flashcard: Flashcard, index: Int, flippedIndex: Int, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .padding(top = 300.dp)
            .size(200.dp)
            .clickable { onClick(index) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDAB6FC)),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (flippedIndex != index) {
                Text(text = flashcard.question, modifier = Modifier.padding(16.dp))
            } else {
                Text(text = flashcard.answer, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
