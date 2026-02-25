package com.example.exercise1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.exercise1.R
import com.example.exercise1.ui.components.AnimatedGiftBox
import kotlinx.coroutines.delay

@Composable
fun RevealScreen(
    onRevealClick: () -> Unit,
    playMusic: () -> Unit
) {
    var isOpening by remember { mutableStateOf(false) }

    val splitTransition = updateTransition(targetState = isOpening, label = "splitScreenExit")
    
    val boxOffsetY by splitTransition.animateFloat(
        transitionSpec = { tween(900, delayMillis = 500, easing = FastOutSlowInEasing) },
        label = "boxFlyUp"
    ) { state -> if (state) -1500f else 0f }

    val textOffsetY by splitTransition.animateFloat(
        transitionSpec = { tween(900, delayMillis = 500, easing = FastOutSlowInEasing) },
        label = "textFlyDown"
    ) { state -> if (state) 1500f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.9f),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedGiftBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .offset(y = boxOffsetY.dp),
                    onOpenStart = { isOpening = true },
                    onOpenComplete = { 
                        playMusic()
                        onRevealClick() 
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Open it!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = textOffsetY.dp)
                )
            }
        }
    }
}
