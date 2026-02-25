package com.example.exercise1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.exercise1.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class StoryItem(
    val imageRes: Int,
    val textRes: Int,
    val animationFlavor: Int
)

@Composable
fun SurpriseScreen() {
    val stories = listOf(
        StoryItem(R.drawable.raisoni, R.string.story_1, 0),
        StoryItem(R.drawable.res_dress, R.string.story_3, 1),
        StoryItem(R.drawable.sky_blue_dress, R.string.story_4, 2)
    )
    val pagerState = rememberPagerState(pageCount = { stories.size })
    val coroutineScope = rememberCoroutineScope()


    var isAnimationVisible by remember { mutableStateOf(false) }
    

    LaunchedEffect(pagerState.currentPage) {

        isAnimationVisible = false
        delay(100)
        
        isAnimationVisible = true
        delay(4000) 
        isAnimationVisible = false
    }


    var isScreenEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isScreenEntered = true
    }


    val confettiComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val activeFlavor = stories[pagerState.currentPage].animationFlavor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->


            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            
            val imageAlpha = 1f - kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
            
            val textSlideX = (pageOffset * 300f)
            
            val scrollState = rememberScrollState()
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            
            // Fade out image as we scroll down (scrollState.value increases)
            val scrollAlpha = 1f - (scrollState.value / 800f).coerceIn(0f, 1f)
            val finalImageAlpha = imageAlpha * scrollAlpha

            Box(modifier = Modifier.fillMaxSize()) {
                
                AnimatedVisibility(
                    visible = isScreenEntered,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1500, delayMillis = 1000)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = finalImageAlpha 
                            translationX = pageOffset * size.width 
                        }
                    ) {

                        Image(
                            painter = painterResource(id = stories[page].imageRes),
                            contentDescription = stringResource(id = R.string.photo_content_description),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )


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


                AnimatedVisibility(
                    visible = isScreenEntered,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 1000, delayMillis = 100, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(500, delayMillis = 100)),
                    modifier = Modifier.fillMaxSize() 
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = textSlideX 
                                alpha = imageAlpha
                            }
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(screenHeight * 0.65f)) // Push text down more so it's cut off, requiring scroll
                        
                        Text(
                            text = stringResource(id = stories[page].textRes),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 36.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                        
                        // Add padding at the bottom so they can scroll a bit past it
                        Spacer(modifier = Modifier.height(180.dp))
                    }
                }
            }
        }


        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(stories.size) { iteration ->
                val isActive = pagerState.currentPage == iteration
                val color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f)
                val width = if (isActive) 32.dp else 12.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color)
                        .size(width = width, height = 8.dp)
                )
            }
        }


        AnimatedVisibility(
            visible = isScreenEntered && (pagerState.currentPage < stories.size - 1),
            enter = fadeIn(animationSpec = tween(500, delayMillis = 2000)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Next", fontWeight = FontWeight.Bold)
            }
        }


        AnimatedVisibility(
            visible = isAnimationVisible,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(1500)),
            modifier = Modifier.fillMaxSize()
        ) {
            when (activeFlavor) {
                0 -> {

                    LottieAnimation(
                        composition = confettiComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        speed = 1.0f
                    )
                }
                1 -> {

                    LottieAnimation(
                        composition = confettiComposition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        speed = 0.7f
                    )
                }
                2 -> {

                    Box(modifier = Modifier.fillMaxSize()) {
                        LottieAnimation(
                            composition = confettiComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            speed = 1.2f
                        )
                        LottieAnimation(
                            composition = confettiComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillWidth,
                            speed = 0.9f
                        )
                    }
                }
            }
        }
    }
}
