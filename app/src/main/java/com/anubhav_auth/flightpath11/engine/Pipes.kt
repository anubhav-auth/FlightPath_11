package com.anubhav_auth.flightpath11.engine

data class Pipe(
    val x: Float,
    val gapY: Float,
    val width: Float = 500f,
    val gapHeight: Float = 100f,
    val passed: Boolean = false
)
