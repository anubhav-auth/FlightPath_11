package com.anubhav_auth.flightpath11.engine

data class Pipe(
    var x: Float,
    var gapY: Float, // Vertical position of the gap between pipes
    val width: Float = 100f,
    val gapHeight: Float = 300f
)
