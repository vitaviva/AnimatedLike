package com.example.animatedlike

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class AnimatedRainbow(
    private val center: Offset,
    private val screenSize: Float,
    private val color: Brush = RainbowColors.random(),
    private val duration: Int = 3000
) {
    private val radius = Animatable(0f)

    suspend fun startAnim() = radius.animateTo(
        targetValue = screenSize * 1.6f,
        animationSpec = tween(durationMillis = duration)
    )

    fun DrawScope.draw() {
        drawCircle(
            brush = color,
            center = this@AnimatedRainbow.center,
            radius = radius.value,
            style =  Stroke((radius.value * 2).coerceAtMost(screenSize)),
        )
    }
}