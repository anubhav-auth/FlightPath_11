package com.anubhav_auth.flightpath11.engine

data class States(
    var birdY:Float = 500f,
    var velocity: Float = 0f,
    var gravity: Float = 1.5f,
    var pipes: MutableList<Pipe> = mutableListOf(),
    var score: Int = 0,
    var highScore: Int = 0,
    var isGameOver: Boolean = false
)