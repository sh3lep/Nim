package ru.spbstu.shelepov.nim.ui

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.spbstu.shelepov.nim.App
import ru.spbstu.shelepov.nim.R
import ru.spbstu.shelepov.nim.database.ScoreDao
import ru.spbstu.shelepov.nim.ui.adapter.ScoreAdapter

//Фрагмент отображения счета игроков, делает запрос к базе и отображает результат.
class ScoreFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_score
    private val scoreAdapter: ScoreAdapter = ScoreAdapter()
    private lateinit var database: ScoreDao

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = (activity?.application as App).database.scoreDao()
        setupRecycler()
    }

    private fun setupRecycler() {
        score_recycler.adapter = scoreAdapter
        val recyclerLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            score_recycler.context,
            recyclerLayoutManager.orientation
        )
        score_recycler.apply {
            setHasFixedSize(true)
            layoutManager = recyclerLayoutManager
            addItemDecoration(dividerItemDecoration)
        }
        GlobalScope.launch {
            val scoreList = async { database.getAll() }
            scoreAdapter.updateScores(*scoreList.await().toTypedArray())
        }
    }
}
