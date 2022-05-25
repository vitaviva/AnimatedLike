package com.example.animatedlike

import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin


class AnimatedFlower(
    private val center: Offset,
    private val color: Array<Color>,
    private val initial: Float,
    private val duration: Int = 400
) {
    private val radius = Animatable(initial)

    suspend fun startAnim() {
        radius.animateTo(0f, keyframes {
            durationMillis = duration
            initial / 3 at 0 with FastOutLinearInEasing
            initial / 8 at (duration * 0.8f).toInt()
        })
    }

    private val sin by lazy { sin(Math.PI / 4).toFloat() }

    private val points
        get() = run {
            val d1 = initial - radius.value
            val d2 = (initial - radius.value) * sin
            arrayOf(
                center.copy(y = center.y - d1),
                center.copy(center.x + d2, center.y - d2),
                center.copy(x = center.x + d1),
                center.copy(center.x + d2, center.y + d2),
                center.copy(y = center.y + d1),
                center.copy(center.x - d2, center.y + d2),
                center.copy(x = center.x - d1),
                center.copy(center.x - d2, center.y - d2),
            )
        }

    fun DrawScope.draw() {
        points.forEachIndexed { index, point ->
            drawCircle(color = color[index % 2], center = point, radius = radius.value)
        }
    }

}
