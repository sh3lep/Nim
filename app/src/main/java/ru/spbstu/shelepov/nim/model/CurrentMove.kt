package ru.spbstu.shelepov.nim.model

/**
 * Модель для хода.
 * @param moveSize - на сколько уменьшить кучу за ход
 * @param heapNumber - какую кучу уменьшить
 */
data class CurrentMove(
    val moveSize: Int,
    val heapNumber: Int
)