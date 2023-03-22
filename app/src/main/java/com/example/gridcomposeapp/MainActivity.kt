package com.example.gridcomposeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gridcomposeapp.ui.theme.GridComposeAppTheme
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    private val viewModel: GridViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GridComposeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CustomGrid(viewModel)
                }
            }
        }
    }
}

@Composable
fun CustomGrid(viewModel: GridViewModel) {

    val playersList = viewModel.playersList.value

    fun checkUpdateTable() {
        for (item in playersList) {
            if (item.playerScoreSum == -1) return
        }
        var point = 1
        val sortList = playersList.sortedByDescending { it.playerScoreSum }
        for (item in sortList) {
            viewModel.changePlayerPoints(item.playerId, point)
            point += 1
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            item { HeadRow() }
            items(playersList.size) { index ->
                CustomRow(
                    playersList[index],
                    { playerId, scoreIndex, score ->
                        viewModel.changePlayerScores(
                            playerId,
                            scoreIndex,
                            score
                        )
                    },
                    { playerId, score -> viewModel.changePlayerAllScores(playerId, score) },
                    { checkUpdateTable() },
                )
            }
        }
    }
}


@Composable
fun HeadRow() {
    Row(modifier = Modifier.fillMaxHeight().background(Color(218,218,218)).padding(vertical = 8.dp)) {
        Box(modifier = Modifier.weight(3f)) {
            Text(text = "Имя")
        }
        Box(modifier = Modifier.weight(2f), contentAlignment = Alignment.Center) {
            Text(text = "Номер")
        }
        for (i in 1..7) {
            Box(modifier = Modifier.weight(1.5f), contentAlignment = Alignment.Center) {
                Text(text = i.toString())
            }
        }
        Box(modifier = Modifier.weight(3f), contentAlignment = Alignment.Center) {
            Text(text = "Сумма очков")
        }
        Box(modifier = Modifier.weight(3f), contentAlignment = Alignment.Center) {
            Text(text = "Место")
        }
    }
}


@Composable
fun CustomRow(
    player: Player,
    changePlayerScore: (Int, Int, String) -> Unit,
    changePlayerAllScore: (Int, Int) -> Unit,
    checkTableUpdate: () -> Unit,
) {

    val context = LocalContext.current

    var playerScoresState by rememberSaveable {
        mutableStateOf(
            listOf("", "", "", "", "", "", "")
        )
    }

    fun playerScoresStateUpdate(index: Int, value: String) {
        val copyList = playerScoresState.toMutableList()
        copyList[index] = value
        playerScoresState = copyList
    }

    LaunchedEffect(
        player.playerScoreSum,
    ) {
        checkTableUpdate()
    }

    fun checkScores(): Int {
        var sumScores = 0
        for (item in playerScoresState.indices) {
            if (playerScoresState[item].isNotEmpty())
                if(playerScoresState[item].toInt() < 6) sumScores += playerScoresState[item].toInt()
                else return -1
            else if (item == player.playerId)
            else return -1
        }
        changePlayerAllScore(player.playerId, sumScores)
        return sumScores
    }

    player.playerScoreSum = checkScores()

    val focusManager = LocalFocusManager.current

    for (i in player.playerScoreList.indices) {
        LaunchedEffect(
            key1 = player.playerScoreList[i],
        ) {
            if (player.playerScoreList[i].isNotEmpty()) {
                if (player.playerScoreList[i].toInt() < 6) {
                    focusManager.moveFocus(
                        focusDirection = FocusDirection.Next,
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Оценка не должна быть больше 5",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    Row(
        modifier = Modifier
            .background(if (player.playerId % 2 == 0) Color.White else Color(218,218,218))
            .padding(5.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(3f)) {
            Text(text = player.name)
        }
        Box(modifier = Modifier.weight(2f), contentAlignment = Alignment.Center) {
            Text(text = player.playerId.toString())
        }
        for (i in 0..6) {
            val textColor = if (playerScoresState[i] != "") {
                if (playerScoresState[i].toInt() > 5 || playerScoresState[i].toInt() < 0) Color.Red else Color.Black
            } else {
                Color.Black
            }

            if (i == player.playerId) {
                Box(modifier = Modifier.weight(1.5f)) {
                    OutlinedTextField(
                        modifier = Modifier,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(35,35,35),
                        ),
                        onValueChange = {},
                        value = "",
                        enabled = false
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1.5f).padding(horizontal = 3.dp)) {
                    OutlinedTextField(
                        modifier = Modifier,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(170,170,170),
                            textColor = textColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = playerScoresState[i],
                        onValueChange = {
                            val pattern = Pattern.compile("[$0-9]")
                            if (it.isNotEmpty()) {
                                if (pattern.matcher(it).matches()) {
                                    changePlayerScore(
                                        player.playerId,
                                        i,
                                        it[it.length - 1].toString()
                                    )
                                    playerScoresStateUpdate(i, it[it.length - 1].toString())
                                }
                            } else {
                                changePlayerScore(player.playerId, i, it)
                                playerScoresStateUpdate(i, it)
                            }
                        },
                    )
                }
            }

        }
        Box(modifier = Modifier.weight(3f), contentAlignment = Alignment.Center) {
            if(player.playerScoreSum != -1) {
                Text(text = player.playerScoreSum.toString(), modifier = Modifier.fillMaxHeight())
            }
        }
        Box(modifier = Modifier.weight(3f), contentAlignment = Alignment.Center) {
            if(player.playerPoint != -1) {
                Text(text = player.playerPoint.toString())
            }
        }
    }
}