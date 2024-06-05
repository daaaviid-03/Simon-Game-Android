package com.example.simongame.game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simongame.ui.theme.SimonGameTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api

class HighScores : ComponentActivity() {
    data class ExerciseResult(val username: String, val score: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    ResultsScreen(results = generateSampleResults())
                }
            }
        }
    }

    private fun generateSampleResults(): List<ExerciseResult> {
        // Generar resultados simulados
        return listOf(
            ExerciseResult("Usuario 1", 100),
            ExerciseResult("Usuario 2", 90),
            ExerciseResult("Usuario 3", 80),
            // Agregar más resultados según sea necesario
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ResultsScreen(results: List<ExerciseResult>) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            Button(onClick = {
                finish()
            }) {
                Text(text = "◀")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Mejores Resultados",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(results) { result ->
                    ResultItem(result = result)
                }
            }
        }
    }

    @Composable
    fun ResultItem(result: ExerciseResult) {
        Card(
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Usuario: ${result.username}")
                Text(text = "Puntuación: ${result.score}")
            }
        }
    }
}

