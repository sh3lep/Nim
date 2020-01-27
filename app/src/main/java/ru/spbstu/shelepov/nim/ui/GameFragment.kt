package ru.spbstu.shelepov.nim.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.spbstu.shelepov.nim.App
import ru.spbstu.shelepov.nim.R
import ru.spbstu.shelepov.nim.database.ScoreDao
import ru.spbstu.shelepov.nim.database.ScoreEntry
import ru.spbstu.shelepov.nim.model.CurrentMove
import ru.spbstu.shelepov.nim.model.GameDifficulty
import ru.spbstu.shelepov.nim.model.GameType
import ru.spbstu.shelepov.nim.viewmodel.GameViewModel

/**
 * Фрагмент где происходит игра, содержит в себе 3 кучи и кнопки
 * для хода. Когда начинается игра, во ViewModel инициируется игровое поле,
 * устанавливаются выбраные настройки игры. "Кучи" подписаны на изменения
 * полей в моделе игры. Т.е. когда они изменяются - интерфейс меняется соот-но.
 * Также есть поле currentMove, когда игрок нажимает на кнопку, формируется
 * соот-ий ход, ViewModel видит это и реагирует.
 */
class GameFragment : BaseFragment() {
    private val currentMove: MutableLiveData<CurrentMove> by lazy {
        MutableLiveData<CurrentMove>()
    }

    private lateinit var viewModel: GameViewModel
    private lateinit var database: ScoreDao

    override val layoutRes: Int
        get() = R.layout.fragment_game

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        database = (activity?.application as App).database.scoreDao()

        viewModel.initGameModel(getGameTypeFromExtras(), getGameDifficultyFromExtras())
        setHeapsObservers()

        currentMove.observe(viewLifecycleOwner, Observer {
            Log.v("tag", "chipIsClicked")
            viewModel.chipIsClicked(it)
        })
        viewModel.winner.observe(viewLifecycleOwner, Observer {
            Log.v("tag", "winner observed")
            when (it) {
                1 -> showWinDialog(1)
                2 -> showWinDialog(2)
            }
        })
    }

    //Подписываемся на изменения куч, меняем текст с анимацей, изменяем кол-во кнопок для хода.
    @SuppressLint("SetTextI18n")
    private fun setHeapsObservers() {
        viewModel.gameModel.firstHeapSize.observe(viewLifecycleOwner, Observer {
            first_heap_title_text.text =
                resources.getString(R.string.first_heap_title) + " $it"
            first_heap_title_text.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fragment_fade_enter
                )
            )
            addChipsToHeaps()
        })
        viewModel.gameModel.secondHeapSize.observe(viewLifecycleOwner, Observer {
            second_heap_title_text.text =
                resources.getString(R.string.second_heap_title) + " $it"
            second_heap_title_text.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fragment_fade_enter
                )
            )
            addChipsToHeaps()
        })
        viewModel.gameModel.thirdHeapSize.observe(viewLifecycleOwner, Observer {
            third_heap_title_text.text =
                resources.getString(R.string.third_heap_title) + " $it"
            third_heap_title_text.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fragment_fade_enter
                )
            )
            addChipsToHeaps()
        })
    }

    //Далее 3 функции для получаения данных об типе игры из предыдущего фрагмента.
    private fun getGameTypeFromExtras(): GameType =
        when (arguments?.getString("game_type") ?: "") {
            GameType.HOTSEAT_GAME.toString() -> GameType.HOTSEAT_GAME
            GameType.COMPUTER_GAME.toString() -> GameType.COMPUTER_GAME
            else -> GameType.HOTSEAT_GAME
        }

    private fun getGameDifficultyFromExtras(): GameDifficulty =
        when (arguments?.getString("game_difficulty") ?: "") {
            GameDifficulty.EASY.toString() -> GameDifficulty.EASY
            GameDifficulty.HARD.toString() -> GameDifficulty.HARD
            else -> GameDifficulty.EASY
        }

    private fun getPlayersNamesFromExtras(playerNumber: Int) =
        when(playerNumber) {
            1 -> arguments?.getString("first_player_name") ?: ""
            2 -> arguments?.getString("second_player_name") ?: ""
            else -> ""
        }


    /* Показываем диалог о победе,
    добавляем данных в базу данных.
     */
    private fun showWinDialog(whoWin: Int) {
        var winner = ""
        var title = ""
        val mainText = R.string.win_dialog_repeat
        when {
            whoWin == 1 -> {
                winner = getPlayersNamesFromExtras(1)
                title = "$winner ${resources.getString(R.string.win)}"
            }
            whoWin == 2 && viewModel.gameModel.gameType == GameType.COMPUTER_GAME -> {
                title = resources.getString(R.string.you_lose)
            }
            else -> {
                winner = getPlayersNamesFromExtras(2)
                title = "$winner ${resources.getString(R.string.win)}"
            }
        }
        val winDialog = AlertDialog.Builder(requireContext())
        winDialog.apply {
            setTitle(title)
            setMessage(mainText)
            setPositiveButton(R.string.win_dialog_repeat_button) { _, _ ->
                val navOptions = NavOptions.Builder()
                navOptions.setPopUpTo(R.id.menuFragment, false)
                val bundle = bundleOf("game_type" to getGameTypeFromExtras().toString(),
                    "game_difficulty" to getGameDifficultyFromExtras(),
                    "first_player_name" to getPlayersNamesFromExtras(1),
                    "second_player_name" to getPlayersNamesFromExtras(2))
                findNavController().navigate(R.id.gameFragment, bundle, navOptions.build())
            }
            setNegativeButton(R.string.to_main_menu) { _, _ ->
                findNavController().popBackStack()
            }
            show()
        }
        GlobalScope.launch {
            if (winner != "") {
                val oldResult = database.getByName(winner)
                if (oldResult == null) {
                    database.insert(ScoreEntry(playerName = winner, score = 10))
                } else {
                    database.updateScore(winner, oldResult.score + 10)
                }
            }
        }
    }

    //Добавляем кол-во кнопок в соот-ии с размером кучи.
    private fun addChipsToHeaps() {
        val chipGroups = listOf(chipgroup_first_heap, chipgroup_second_heap, chipgroup_third_heap)
        chipGroups.forEach { it.removeAllViews() }

        val heapSizes = listOf(
            first_heap_title_text,
            second_heap_title_text,
            third_heap_title_text
        ).map { (it.text.toString().last() - '0') }

        chipGroups.forEachIndexed { index, item ->
            for (i in heapSizes[index] downTo 1) {
                val chip = Chip(requireContext(), null, R.attr.chipStyle)
                chip.text = "  $i  "
                chip.setOnClickListener {
                    currentMove.value = CurrentMove(i, index + 1)
                }
                item.addView(chip)
            }
        }
    }
}
