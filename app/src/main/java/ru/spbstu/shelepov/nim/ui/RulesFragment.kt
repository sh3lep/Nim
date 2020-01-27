package ru.spbstu.shelepov.nim.ui


import android.os.Bundle
import ru.spbstu.shelepov.nim.R

//Фрагмент для правил, просто показывает правила игры.
class RulesFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_rules

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
