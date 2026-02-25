package com.example.exercise1

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exercise1.ui.screens.RevealScreen
import com.example.exercise1.ui.screens.SurpriseScreen
import com.example.exercise1.ui.screens.BirthdayMessageScreen
import com.example.exercise1.ui.screens.JourneyScreen
import com.example.exercise1.ui.theme.Exercise1Theme

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mediaPlayer = MediaPlayer.create(this, R.raw.happy_birthday)
        mediaPlayer?.isLooping = true
        
        enableEdgeToEdge()
        setContent {
            Exercise1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BirthdayAppNavHost(
                        playMusic = { 
                            if (mediaPlayer?.isPlaying == false) {
                                mediaPlayer?.start() 
                            }
                        },
                        stopMusic = {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume ONLY if it was previously playing (managed manually, or simply don't auto-start here if it hasn't started yet)
        // If we want it to only start on click, maybe we shouldn't auto-start in onResume if the user hasn't clicked yet.
        // But if they clicked, backgrounded, and returned, it should resume.
        // A simple fix: only resume if the current index > 0, or just let it resume if it was already "started".
        // For now, let's just not auto-start on onResume initially. 
        // We will rely on the `playMusic` lambda to kick it off.
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun BirthdayAppNavHost(
    playMusic: () -> Unit,
    stopMusic: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "reveal") {
        composable("reveal") {
            RevealScreen(
                onRevealClick = {
                    navController.navigate("birthday_message") {
                        popUpTo("reveal") { inclusive = true }
                    }
                },
                playMusic = playMusic
            )
        }
        composable("birthday_message") {
            BirthdayMessageScreen(
                onNextClick = {
                    // Stop music when transitioning to stories
                    stopMusic()
                    
                    navController.navigate("journey") {
                        popUpTo("birthday_message") { inclusive = true }
                    }
                }
            )
        }
        composable("journey") {
            JourneyScreen(
                onNextClick = {
                    navController.navigate("surprise") {
                        popUpTo("journey") { inclusive = true }
                    }
                }
            )
        }
        composable("surprise") {
            SurpriseScreen()
        }
    }
}