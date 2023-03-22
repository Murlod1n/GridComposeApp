package com.example.gridcomposeapp

data class Player(
    val name: String,
    val playerId: Int,
    val playerScoreList: MutableList<String> = mutableListOf("","","","","","",""),
    var playerScoreSum: Int = -1,
    var playerPoint: Int = -1
)
