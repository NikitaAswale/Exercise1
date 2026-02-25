package com.example.exercise1.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class BoxState { Idle, Anticipating, Popping }

@Composable
fun AnimatedGiftBox(
    modifier: Modifier = Modifier,
    onOpenStart: () -> Unit,
    onOpenComplete: () -> Unit
) {
    var boxState by remember { mutableStateOf(BoxState.Idle) }
    val coroutineScope = rememberCoroutineScope()
    
    val transition = updateTransition(targetState = boxState, label = "boxTransition")
    
    val boxScaleY by transition.animateFloat(
        transitionSpec = {
            when {
                BoxState.Idle isTransitioningTo BoxState.Anticipating -> tween(300, easing = EaseOutBack)
                BoxState.Anticipating isTransitioningTo BoxState.Popping -> spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
                else -> spring()
            }
        },
        label = "boxScaleY"
    ) { state ->
        when (state) {
            BoxState.Idle -> 1f
            BoxState.Anticipating -> 0.7f
            BoxState.Popping -> 1.1f
        }
    }
    
    val boxScaleX by transition.animateFloat(
        transitionSpec = {
            when {
                BoxState.Idle isTransitioningTo BoxState.Anticipating -> tween(300)
                BoxState.Anticipating isTransitioningTo BoxState.Popping -> spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
                else -> spring()
            }
        },
        label = "boxScaleX"
    ) { state ->
        when (state) {
            BoxState.Idle -> 1f
            BoxState.Anticipating -> 1.2f
            BoxState.Popping -> 0.9f
        }
    }

    val lidOffsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState == BoxState.Popping) tween(600, easing = EaseOutCirc)
            else tween(200)
        },
        label = "lidOffsetY"
    ) { state ->
        if (state == BoxState.Popping) -250f else 0f
    }
    
    val lidRotation by transition.animateFloat(
        transitionSpec = {
            if (targetState == BoxState.Popping) tween(600, easing = EaseOutCirc)
            else tween(200)
        },
        label = "lidRotation"
    ) { state ->
        if (state == BoxState.Popping) 45f else 0f
    }
    
    val boxOffsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState == BoxState.Popping) spring(dampingRatio = 0.5f, stiffness = 100f)
            else tween(200)
        },
        label = "boxOffsetY"
    ) { state ->
        if (state == BoxState.Popping) -50f else 0f
    }

    val particles = remember { List(30) { Particle() } }
    val particleProgress by transition.animateFloat(
        transitionSpec = {
            if (targetState == BoxState.Popping) tween(800, easing = EaseOutExpo)
            else tween(0)
        },
        label = "particles"
    ) { state ->
        if (state == BoxState.Popping) 1f else 0f
    }
    
    val infiniteTransition = rememberInfiniteTransition()
    val idleWobble by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = modifier
            .size(250.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (boxState == BoxState.Idle) {
                    coroutineScope.launch {
                        onOpenStart()
                        boxState = BoxState.Anticipating
                        delay(200)
                        boxState = BoxState.Popping
                        delay(1200)
                        onOpenComplete()
                    }
                }
            }
    ) {
        val centerX = size.width / 2
        val baseY = size.height * 0.75f
        
        val boxWidth = 140.dp.toPx()
        val boxHeight = 110.dp.toPx()
        val lidWidth = 160.dp.toPx()
        val lidHeight = 35.dp.toPx()
        
        val rotation = if (boxState == BoxState.Idle) idleWobble else 0f
        
        translate(top = boxOffsetY) {
            rotate(rotation, pivot = Offset(centerX, baseY)) {
                scale(scaleX = boxScaleX, scaleY = boxScaleY, pivot = Offset(centerX, baseY)) {
                    
                    val boxLeft = centerX - boxWidth / 2
                    val boxTop = baseY - boxHeight
                    
                    drawRoundRect(
                        color = Color(0xFFE91E63),
                        topLeft = Offset(boxLeft, boxTop),
                        size = Size(boxWidth, boxHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                    
                    if (boxState == BoxState.Popping) {
                        drawRoundRect(
                            color = Color(0xFF880E4F),
                            topLeft = Offset(boxLeft + 10f, boxTop + 10f),
                            size = Size(boxWidth - 20f, boxHeight * 0.2f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }

                    val ribbonWidth = 24.dp.toPx()
                    drawRect(
                        color = Color(0xFFFFC107),
                        topLeft = Offset(centerX - ribbonWidth / 2, boxTop),
                        size = Size(ribbonWidth, boxHeight)
                    )
                    
                    drawRect(
                        color = Color.Black.copy(alpha = 0.15f),
                        topLeft = Offset(boxLeft, boxTop),
                        size = Size(boxWidth, boxHeight / 5)
                    )
                }
            }

            if (particleProgress > 0f) {
                particles.forEach { p ->
                    val distance = p.maxDistance * particleProgress
                    val px = centerX + cos(p.angle) * distance
                    val py = (baseY - boxHeight) + sin(p.angle) * distance + (particleProgress * particleProgress * 200f)
                    val pScale = 1f - particleProgress
                    
                    if (pScale > 0) {
                        drawCircle(
                            color = p.color.copy(alpha = pScale),
                            radius = p.size * pScale,
                            center = Offset(px, py)
                        )
                    }
                }
            }

            rotate(rotation, pivot = Offset(centerX, baseY)) {
                translate(top = lidOffsetY, left = lidOffsetY * 0.2f) {
                    rotate(lidRotation, pivot = Offset(centerX, baseY - boxHeight)) {
                        scale(scaleX = boxScaleX, scaleY = boxScaleY, pivot = Offset(centerX, baseY)) {
                            val lidLeft = centerX - lidWidth / 2
                            val lidTop = baseY - boxHeight - lidHeight + 5f
                            
                            drawRoundRect(
                                color = Color(0xFFD81B60),
                                topLeft = Offset(lidLeft, lidTop),
                                size = Size(lidWidth, lidHeight),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                            
                            val lRibbonWidth = 24.dp.toPx()
                            drawRect(
                                color = Color(0xFFFFC107),
                                topLeft = Offset(centerX - lRibbonWidth / 2, lidTop),
                                size = Size(lRibbonWidth, lidHeight)
                            )
                            
                            drawBow(
                                center = Offset(centerX, lidTop),
                                color = Color(0xFFFFC107),
                                size = 45.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

fun DrawScope.drawBow(center: Offset, color: Color, size: Float) {
    val bowPath = Path().apply {
        moveTo(center.x, center.y)
        cubicTo(
            center.x - size, center.y - size * 1.2f,
            center.x - size * 1.5f, center.y + size * 0.5f,
            center.x, center.y
        )
        moveTo(center.x, center.y)
        cubicTo(
            center.x + size, center.y - size * 1.2f,
            center.x + size * 1.5f, center.y + size * 0.5f,
            center.x, center.y
        )
    }
    
    drawPath(path = bowPath, color = color)
    
    drawCircle(
        color = color,
        radius = size * 0.2f,
        center = center
    )
    
    drawCircle(
        color = Color(0xFFE6A800),
        radius = size * 0.1f,
        center = center
    )
}

class Particle {
    val angle = Random.nextFloat() * Math.PI.toFloat() * 2
    val maxDistance = Random.nextFloat() * 400f + 100f
    val size = Random.nextFloat() * 15f + 8f
    val color = listOf(
        Color(0xFFFFD700),
        Color(0xFFFF4081),
        Color(0xFF00E676),
        Color(0xFF29B6F6),
        Color.White
    ).random()
}
