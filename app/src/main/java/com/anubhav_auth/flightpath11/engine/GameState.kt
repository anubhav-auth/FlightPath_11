package com.anubhav_auth.flightpath11.engine

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GameState(
    val birdY:Float = 0f,
    val birdSize:Dp = 30.dp,
    val gravity:Float = 0.27f,
    val velocity:Float = 0f,
    val flapImpulse:Float = -6f,
    val frameDelay:Long = 16L,
    val pipeSpeed: Float = 3.2f,
    val pipes: List<Pipe> = emptyList(),
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val collisionPosition: Pair<Float, Float>? = null
)
