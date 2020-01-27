package ru.spbstu.shelepov.nim.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.spbstu.shelepov.nim.extensions.revert

/**
 * Класс, который используется как модель игры.
 * @param firstHeapSize - размер первой кучи
 * 2 и 3 аналогично
 * @param gameType - тип игры, бот или с игроком
 * @param gameDifficulty - сложность игры с ботом
 * Содержит в себе builder для удобного создания класса.
 */
class GameModel
    (
    var firstHeapSize: MutableLiveData<Int> = MutableLiveData(3),
    var secondHeapSize: MutableLiveData<Int> = MutableLiveData(5),
    var thirdHeapSize: MutableLiveData<Int> = MutableLiveData(7),
    val gameType: GameType,
    val gameDifficulty: GameDifficulty
) {
    private constructor(builder: Builder) : this(
        builder.firstHeapSize,
        builder.secondHeapSize,
        builder.thirdHeapSize,
        builder.gameType,
        builder.gameDifficulty
    )

    companion object {
        inline fun build(gameType: GameType, block: Builder.() -> Unit = {}) =
            Builder(gameType).apply(block).build()
    }

    class Builder(
        val gameType: GameType
    ) {
        var firstHeapSize: MutableLiveData<Int> = MutableLiveData(3)
        var secondHeapSize: MutableLiveData<Int> = MutableLiveData(5)
        var thirdHeapSize: MutableLiveData<Int> = MutableLiveData(7)
        var gameDifficulty: GameDifficulty = GameDifficulty.EASY

        fun build() = GameModel(this)
    }

    val FIRST_PLAYER_MOVE = 1
    val SECOND_PLAYER_MOVE = 2

    var whichPlayerMove: Int = FIRST_PLAYER_MOVE
        private set

    /*Вызывается когда реальный игрок совершил свой ход,
    и необходимо сделать изменения в модели, а также
    проверить на победу.
     */
    fun makeMove(currentMove: CurrentMove): Int {
        decreaseHeapSize(currentMove)
        val winResult = checkForWin()
        whichPlayerMove = whichPlayerMove.revert()
        return winResult
    }

    //Вызывается когда необходимо сделать ход компьютера
    fun makeComputerMove(): Int {
        val move = getComputerMove()
        Log.v("tag", "computer move: $move")
        decreaseHeapSize(move)
        val winResult = checkForWin()
        whichPlayerMove = whichPlayerMove.revert()
        return winResult
    }

    /*Алгоритм для компьютерного хода
    Идет обход всех возможных ходов, при этом вычисляется
    сумма по модулю 2 всех куч после хода игрока. И если
    эта сумма равна нулю, то ход "победный".
    Если такого хода нет, то совершается случайный ход.
    Также если сложность легкая есть вероятность 50%, что
    компьютер сделает случайный ход.
     */
    private fun getComputerMove(): CurrentMove {
        fun getRandomMove(heaps: List<Int>): CurrentMove {
            val heapSize = heaps.filter { it != 0 }.random()
            return CurrentMove((1..heapSize).random(), heaps.indexOf(heapSize) + 1)
        }
        val heaps =
            listOf(firstHeapSize.value, secondHeapSize.value, thirdHeapSize.value).map { it ?: 0 }
        heaps.forEachIndexed { index, heapSize ->
            for (i in 0 until heapSize) {
                val tmpList = heaps.toMutableList()
                tmpList.remove(heapSize)
                val result = tmpList.fold(i) { total, next -> total.xor(next) }
                if (result == 0) {
                    return if (gameDifficulty == GameDifficulty.EASY && (0..1).random() == 1) {
                        getRandomMove(heaps)
                    } else {
                        CurrentMove(heapSize - i, index + 1)
                    }
                }
            }
        }
        return getRandomMove(heaps)
    }

    /*
    Уменьшает размер куч, оповещает observer'ов об этом.
     */
    private fun decreaseHeapSize(currentMove: CurrentMove) {
        when (currentMove.heapNumber) {
            1 -> {
                val newResult = firstHeapSize.value?.minus(currentMove.moveSize)
                firstHeapSize.value = (newResult)
            }
            2 -> {
                val newResult = secondHeapSize.value?.minus(currentMove.moveSize)
                secondHeapSize.value = (newResult)
            }
            3 -> {
                val newResult = thirdHeapSize.value?.minus(currentMove.moveSize)
                thirdHeapSize.value = (newResult)
            }
        }
    }

    //Проверка на победу
    private fun checkForWin(): Int {
        val isWin =
            firstHeapSize.value == 0 && secondHeapSize.value == 0 && thirdHeapSize.value == 0
        return if (isWin) whichPlayerMove else -1
    }
}
