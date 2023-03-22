package com.example.gridcomposeapp
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel


class GridViewModel: ViewModel() {

    val playersList: MutableState<List<Player>> = mutableStateOf(
        listOf(
            Player("Участник 1", 0),
            Player("Участник 2", 1),
            Player("Участник 3", 2),
            Player("Участник 4", 3),
            Player("Участник 5", 4),
            Player("Участник 6", 5),
            Player("Участник 7", 6)
        )
    )

    fun changePlayerScores(playerId: Int, scoreIndex: Int, score: String) {
        val copyList = playersList.value.map { it.copy() }
        copyList[playerId].playerScoreList[scoreIndex] = score
        playersList.value = copyList
    }

    fun changePlayerAllScores(playerId: Int, score: Int) {

        val copyList = playersList.value.map { it.copy() }
        copyList[playerId].playerScoreSum = score
        playersList.value = copyList
    }

    fun changePlayerPoints(playerId: Int, point: Int) {
        val copyList = playersList.value.map { it.copy() }
        copyList[playerId].playerPoint = point
        playersList.value = copyList
    }
}