package com.example.animatedlike;

import android.content.res.Resources
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.imageResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


class AnimatedEmoji(
    private val start: Offset,
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val res: Resources,
    private val duration: Int = 1500
) {

    val img by lazy {
        ImageBitmap.imageResource(res, EmojiImages.random())
    }

    private val throwDistance by lazy {
        ((start.x - 1.5 * screenWidth).toInt()..(start.x + 0.5 * screenWidth).toInt()).random()
    }
    private val throwHeight by lazy {
        ((start.y * 0.5f).toInt()..(start.y * 0.9f).toInt()).random()
    }

    private val x = Animatable(start.x)
    private val y = Animatable(start.y)
    private val rotate = Animatable(0f)
    private val alpha = Animatable(1f)
    val offset get() = Offset(x.value, y.value)

    suspend fun CoroutineScope.startAnim() {
        async {
            rotate.animateTo(
                360f, infiniteRepeatable(
                    animation = tween(duration / 2, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        awaitAll(
            async {
                x.animateTo(
                    throwDistance.toFloat(),
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                )
            },
            async {
                y.animateTo(
                    throwHeight.toFloat(),
                    animationSpec = tween(
                        duration / 2,
                        easing = LinearOutSlowInEasing
                    )
                )
                y.animateTo(
                    (throwHeight..screenHeight.toInt()).random().toFloat(),
                    animationSpec = tween(
                        duration / 2,
                        easing = FastOutLinearInEasing
                    )
                )
            },
            async {
                alpha.animateTo(
                    0.5f,
                    tween(duration, easing = CubicBezierEasing(1f, 0f, 1f, 0.8f))
                )
            }
        )
    }

    private val d by lazy {
        Offset(img.width / 2f, img.height / 2f)
    }

    fun DrawScope.draw() {
        rotate(rotate.value, pivot = offset) {
            drawImage(
                image = img,
                topLeft = offset - d,
                alpha = alpha.value,
            )
        }
    }
}
