package com.example.animatedlike;

import android.content.res.Resources
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch


@Composable
fun AnimatedLike(modifier: Modifier = Modifier, state: AnimatedLikeState = rememberAnimatedLikeState()) {

    LaunchedEffect(Unit) {
        snapshotFlow { state.animatedEmojis.takeLast(EmojiCnt) }
            .flatMapMerge { it.asFlow() }
            .collect {
                launch {
                    with(it) {
                        startAnim()
                        state.animatedEmojis.remove(it)
                    }
                    val anim = AnimatedFlower(
                        center = it.offset,
                        color = Palette.from(it.img.asAndroidBitmap()).generate().let {
                            arrayOf(
                                Color(it.getDominantColor(Color.Transparent.toArgb())),
                                Color(it.getVibrantColor(Color.Transparent.toArgb()))
                            )
                        },
                        initial = it.img.run { width.coerceAtLeast(height) / 2 }.toFloat()
                    )
                    state.animatedFlowers.add(anim)
                    anim.startAnim()
                    state.animatedFlowers.remove(anim)
                }
            }
    }


    LaunchedEffect(Unit) {

        snapshotFlow { state.animatedRainbow.lastOrNull() }
            .filterNotNull()
            .collect {
                launch {
                    val result = it.startAnim()
                    if (result.endReason == AnimationEndReason.Finished) {
                        state.animatedRainbow.remove(it)
                    }
                }
            }
    }


    Canvas(modifier.fillMaxSize()) {

        state.animatedRainbow.forEach { animatable ->
            with(animatable) { draw() }
        }

        state.animatedEmojis.forEach { animatable ->
            with(animatable) { draw() }
        }

        state.animatedFlowers.forEach { animatable ->
            with(animatable) { draw() }
        }
    }
}


class AnimatedLikeState(
    private val screenWidthPx: Float,
    private val screenHeightPx: Float,
    private val res: Resources
) {
    internal val animatedRainbow = mutableStateListOf<AnimatedRainbow>()
    internal val animatedEmojis = mutableStateListOf<AnimatedEmoji>()
    internal val animatedFlowers = mutableListOf<AnimatedFlower>()

    private var idx = 0L
    fun onTap(offset: Offset) {
        animatedRainbow.add(
            AnimatedRainbow(
                offset,
                screenHeightPx.coerceAtLeast(screenWidthPx),
                RainbowColors[(idx++ % RainbowColors.size).toInt()]
            )
        )
        animatedEmojis.addAll(buildList {
            repeat(EmojiCnt) {
                add(AnimatedEmoji(offset, screenWidthPx, screenHeightPx, res))
            }
        })
    }
}


const val ColorAlpha = 0.6f
val Red = Color(0xFFFF7166).copy(alpha = ColorAlpha)
val Blue = Color(0xFF6BC9F1).copy(alpha = ColorAlpha)
val LightBlue = Color(0xFF52DDDE).copy(alpha = ColorAlpha)
val Green = Color(0xFF52DE9D).copy(alpha = ColorAlpha)
val Yellow = Color(0xFFFFD668).copy(alpha = ColorAlpha)
val Orange = Color(0xFFE59768).copy(alpha = ColorAlpha)
val LightYellow = Color(0xFFFEE8C1).copy(alpha = ColorAlpha)

val RainbowColors = arrayOf(
//    Brush.linearGradient(listOf(Red, Orange)),
//    Brush.linearGradient(listOf(Orange, Yellow)),
//    Brush.linearGradient(listOf(Yellow, LightYellow)),
//    Brush.linearGradient(listOf(Yellow, Green)),
//    Brush.linearGradient(listOf(Green, LightBlue)),
//    Brush.linearGradient(listOf(LightBlue, Blue)),
//    Brush.linearGradient(listOf(Orange, Red)),

    Brush.linearGradient(listOf(Red, LightBlue)),
    Brush.linearGradient(listOf(LightBlue, Yellow)),
    Brush.linearGradient(listOf(Yellow, Green)),
    Brush.linearGradient(listOf(Green, Orange)),
    Brush.linearGradient(listOf(Orange, Blue)),
    Brush.linearGradient(listOf(Blue, LightYellow)),
    Brush.linearGradient(listOf(LightYellow, Red)),
)

val EmojiImages = arrayOf(
    R.drawable.bag,
    R.drawable.camera,
    R.drawable.glasses,
    R.drawable.icecream,
    R.drawable.juice,
    R.drawable.palm,
    R.drawable.parasol,
    R.drawable.shorts,
    R.drawable.slippers,
    R.drawable.sun,
    R.drawable.watermelon,
    R.drawable.wave,
)
const val EmojiCnt = 5


@Composable
fun rememberAnimatedLikeState() = run {
    val screenHeightPx = LocalDensity.current.run {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val screenWidthPx = LocalDensity.current.run {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val res = LocalContext.current.resources

    remember { AnimatedLikeState(screenWidthPx, screenHeightPx, res) }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val animatedLikeState = rememberAnimatedLikeState()
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                animatedLikeState.onTap(offset)
            }
        }
    ) {
        AnimatedLike(state = animatedLikeState)
    }

}
