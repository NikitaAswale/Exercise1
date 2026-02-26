package com.example.exercise1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.exercise1.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MiddleScreen(onNextClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.toFloat()

    var isBackgroundVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }
    
    // Text starts at the absolute bottom of the screen.
    val textOffsetY = remember { Animatable(screenHeight) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        isBackgroundVisible = true
        
        // Small delay before text starts sliding so background has time to fade in
        delay(700)
        
        // Run slide and fade-in together in parallel
        launch {
            textOffsetY.animateTo(
                targetValue = screenHeight / 2f - 60f,
                animationSpec = tween(durationMillis = 8000, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000)
            )
        }
        
        // Wait for the slide to finish (8s), then show button
        delay(8000)
        delay(800)
        isButtonVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // 1. Background image with gradients — fades in
        AnimatedVisibility(
            visible = isBackgroundVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1500)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.happy_birthday),
                    contentDescription = "Happy Birthday",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Bottom Dark Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                        )
                )

                // Top Dark Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // 2. "Happy Birthday" Text — NO AnimatedVisibility wrapper.
        // Rendered directly in the Box so nothing clips it.
        // It starts at `screenHeight` dp (absolute bottom, off-screen) and slides up.
        Text(
            text = "Many Many Happy Returns of the Day Nikita !! 🎉💖",
            style = MaterialTheme.typography.displaySmall.copy(
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp)
                .offset(y = textOffsetY.value.dp)
                .alpha(textAlpha.value)
        )

        // 3. Delayed Fade-In Content: "1 more surprise" Button
        AnimatedVisibility(
            visible = isButtonVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "1 more surprise for you ...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
        
        // --- Overlay Animations ---
        
        // 1. Confetti Animation
        val confettiComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
        AnimatedVisibility(
            visible = isBackgroundVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 2000)),
            modifier = Modifier.fillMaxSize()
        ) {
            LottieAnimation(
                composition = confettiComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                speed = 1.0f
            )
        }
    }
}
