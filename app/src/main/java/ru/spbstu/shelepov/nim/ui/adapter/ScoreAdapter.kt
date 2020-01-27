package ru.spbstu.shelepov.nim.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.score_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.spbstu.shelepov.nim.R
import ru.spbstu.shelepov.nim.database.ScoreEntry

//Адаптер для отображения счета игроков
class ScoreAdapter :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    private val scoreList = mutableListOf<ScoreEntry>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.score_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int =
        scoreList.size

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) =
        holder.bind(scoreList[position])

    /*Обновление счета, идет обращение к базе данных в
    фоновом потоке, а затем значения устанавливаются в основном.
     */
    suspend fun updateScores(vararg scoreEntry: ScoreEntry) {
        scoreEntry.forEach {
            scoreList.add(it)
        }
        scoreList.sortByDescending { it.score }
        withContext(Dispatchers.Main) {
            notifyDataSetChanged()
        }
    }

    inner class ScoreViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(scoreEntry: ScoreEntry) {
            scoreEntry.apply {
                view.place_text.text = (this@ScoreViewHolder.layoutPosition + 1).toString()
                view.player_name_text.text = scoreEntry.playerName
                view.score_text.text = scoreEntry.score.toString()
            }
        }
    }

}