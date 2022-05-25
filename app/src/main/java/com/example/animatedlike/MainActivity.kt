package com.example.animatedlike

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.animatedlike.ui.theme.AnimatedLikeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AnimatedLikeTheme {

                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(Color.Transparent, true)
                }
                val animatedlikeState = rememberAnimatedLikeState()
                val coroutineScope = rememberCoroutineScope()
                lateinit var job: Job
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInteropFilter {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    job = coroutineScope.launch {
                                        while (true) {
                                            animatedlikeState.onTap(Offset(it.x, it.y))
                                            delay(200)
                                        }
                                    }
                                }
                                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                                    job.cancel()
                                }
                            }
                            true
                        },
                    color = MaterialTheme.colorScheme.background
                ) {
                    Image(painter = painterResource(id = R.drawable.bg), "")
                    AnimatedLike(modifier = Modifier.fillMaxSize(), animatedlikeState)
                }

            }
        }
    }
}
