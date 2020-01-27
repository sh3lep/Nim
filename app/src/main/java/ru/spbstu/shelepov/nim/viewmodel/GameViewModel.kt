package ru.spbstu.shelepov.nim.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.spbstu.shelepov.nim.model.CurrentMove
import ru.spbstu.shelepov.nim.model.GameDifficulty
import ru.spbstu.shelepov.nim.model.GameModel
import ru.spbstu.shelepov.nim.model.GameType

/**
 * ViewModel для игры, содержит в себе модель игры, обрабатывает события
 * из игрового фрагмента.
 */
class GameViewModel : ViewModel() {

    lateinit var gameModel: GameModel
    var winner: MutableLiveData<Int> = MutableLiveData(-1)

    fun initGameModel(
        gameType: GameType,
        gameDifficulty: GameDifficulty
    ) {
        gameModel = GameModel.build(gameType) {
            this.gameDifficulty = gameDifficulty
        }
    }

    fun chipIsClicked(currentMove: CurrentMove) {
        winner.value = gameModel.makeMove(currentMove)
        Log.v("tag", "player move: $currentMove")
        if (gameModel.gameType == GameType.COMPUTER_GAME && winner.value == -1) {
            GlobalScope.launch {
                delay(300)
                withContext(Dispatchers.Main) {
                    winner.value = (gameModel.makeComputerMove())
                }
            }
        }
    }

}