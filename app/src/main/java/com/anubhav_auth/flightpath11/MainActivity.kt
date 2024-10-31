package com.anubhav_auth.flightpath11

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anubhav_auth.flightpath11.engine.GameAssets
import com.anubhav_auth.flightpath11.engine.GameState
import com.anubhav_auth.flightpath11.engine.Pipe
import com.anubhav_auth.flightpath11.ui.theme.FlightPath11Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        enableEdgeToEdge()
        setContent {

            FlightPath11Theme {

                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->


                    FlappyBirdGame(modifier = Modifier.padding())

                }
            }
        }
    }
}

@Composable
fun FlappyBirdGame(modifier: Modifier = Modifier) {
    var gameState by remember { mutableStateOf(GameState()) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                if (!gameState.isGameOver) {
                    gameState = gameState.copy(velocity = gameState.flapImpulse)
                }
            }
    ) {
        val maxHeight = maxHeight.value
        val birdX = maxWidth.value * 0.3f
        Text(
            text = "Score: ${gameState.score}",
            color = Color.White, // Or any color you prefer
            modifier = Modifier
                .align(Alignment.TopStart) // Position in the top-left corner
                .padding(16.dp) // Add some padding
        )

//        bird
        LaunchedEffect(key1 = !gameState.isGameOver) {
            while (true) {
                if (!gameState.isGameOver) {
                    gameState = gameState.copy(
                        velocity = gameState.velocity + gameState.gravity,
                        birdY = gameState.birdY + gameState.velocity
                    )
                    if (gameState.birdY > maxHeight - gameState.birdSize.value) {
                        gameState =
                            gameState.copy(
                                birdY = maxHeight - gameState.birdSize.value,
                                velocity = 0f
                            )
                    } else if (gameState.birdY < 0f) {
                        gameState = gameState.copy(birdY = 0f, velocity = 0f)
                    }


                    if (checkCollision(
                            birdX,
                            gameState.birdY,
                            gameState.birdSize,
                            gameState.pipes
                        )
                    ) {
                        gameState = gameState.copy(isGameOver = true)
                        break
                    }

                }
                delay(gameState.frameDelay)
            }
        }

//        pipes
        LaunchedEffect(key1 = !gameState.isGameOver) {
            while (true) {
                if (!gameState.isGameOver) {
                    val updatedPipes = gameState.pipes.map { pipe ->
                        pipe.copy(x = pipe.x - gameState.pipeSpeed)
                    }.filter { pipe -> pipe.x > -pipe.width }.toMutableList()

                    if ((updatedPipes.lastOrNull()?.x
                            ?: 0f) < 650f
                    ) {
                        updatedPipes += createPipe(
                            maxWidth.value * 1.3f,
                            maxHeight
                        )
                    }

                    gameState = gameState.copy(pipes = updatedPipes)

                    if (gameState.pipes.any { pipe ->
                            birdX > pipe.x && birdX < pipe.x + pipe.width && !pipe.passed
                        }) {
                        gameState = gameState.copy(
                            pipes = gameState.pipes.map {
                                if (birdX > it.x && birdX < it.x + it.width && !it.passed) it.copy(passed = true) else it
                            },
                            score = gameState.score + 1
                        )
                    }
                }

                delay(gameState.frameDelay)
            }
        }

        gameState.pipes.forEach { pipe ->
            Pipe(pipe = pipe)
        }

        GetBird(
            modifier = Modifier
                .offset(y = gameState.birdY.dp, x = (maxWidth.value * 0.3).dp)
                .size(gameState.birdSize)
        )
        if (gameState.isGameOver) {
            GameOverScreen(modifier = Modifier.fillMaxSize(), gameState.score, onRestart = {
                gameState = gameState.copy(
                    isGameOver = false,
                    birdY = 0f,
                    velocity = 0f,
                    pipes = emptyList(),
                    score = 0
                )
            }) // Show game over screen
        }
    }

}

@Composable
fun Pipe(pipe: Pipe, modifier: Modifier = Modifier) {
    // Top pipe
    Image(
        painter = painterResource(GameAssets.topPipe),
        contentDescription = "Top Pipe",
        modifier = modifier
            .offset(x = pipe.x.dp, y = (pipe.gapY - 320).dp) // Adjust offset to fit screen
            .size(80.dp, 450.dp) // Adjust size as needed
    )

    // Bottom pipe
    Image(
        painter = painterResource(GameAssets.bottomPipe),
        contentDescription = "Bottom Pipe",
        modifier = modifier
            .offset(x = pipe.x.dp, y = (pipe.gapY + pipe.gapHeight).dp) // Place below gap
            .size(80.dp, 450.dp)
    )
}


fun createPipe(xPosition: Float, maxHeight: Float): Pipe {

    val minGapHeight = 50f
    val maxGapHeight = maxHeight - 150f

    val gapYPosition =
        (minGapHeight.toInt()..maxGapHeight.toInt()).random().toFloat() // Adjust range as needed
    return Pipe(x = xPosition, gapY = gapYPosition)
}

@Composable
fun GetBird(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(GameAssets.bird),
        contentDescription = "Bird",
        modifier = modifier
    )
}

fun intersects(rect1: Rect, rect2: Rect): Boolean {
    return rect1.left < rect2.right &&
            rect1.right > rect2.left &&
            rect1.top < rect2.bottom &&
            rect1.bottom > rect2.top
}

fun checkCollision(birdX: Float, birdY: Float, birdSize: Dp, pipes: List<Pipe>): Boolean {
    // Create bird rectangle
    val birdRect = getBirdRect(birdX, birdY, birdSize)

    for (pipe in pipes) {
        val (topPipeRect, bottomPipeRect) = getPipeRect(pipe)

        // Check for intersection with top pipe
        if (intersects(birdRect, topPipeRect)) {
            return true // Collision with top pipe
        }

        // Check for intersection with bottom pipe
        if (intersects(birdRect, bottomPipeRect)) {
            return true // Collision with bottom pipe
        }
    }

    return false // No collisions detected
}

fun getBirdRect(birdX: Float, birdY: Float, birdSize: Dp): Rect {
    return Rect(
        left = birdX,
        top = birdY,
        right = birdX + birdSize.value,
        bottom = birdY + birdSize.value
    )
}

fun getPipeRect(pipe: Pipe): Pair<Rect, Rect> {
    val topPipeRect = Rect(
        left = pipe.x,
        top = pipe.gapY - 360f, // Adjust as needed based on pipe height
        right = pipe.x + 80f,  // Assuming pipe width is 80.dp
        bottom = pipe.gapY + 20f      // The bottom edge of the top pipe
    )

    val bottomPipeRect = Rect(
        left = pipe.x,
        top = pipe.gapY + pipe.gapHeight + 45f, // Adjust as needed for gap
        right = pipe.x + 80f,
        bottom = pipe.gapY + pipe.gapHeight + 450f // Adjust based on pipe height
    )

    return Pair(topPipeRect, bottomPipeRect)
}


@Composable
fun GameOverScreen(modifier: Modifier = Modifier, score: Int, onRestart: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        // Gradient background
        Box(
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Red, Color.Transparent),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        Text(text = " score: $score")

        Button(
            onClick = { onRestart() },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Restart")
        }
    }
}

