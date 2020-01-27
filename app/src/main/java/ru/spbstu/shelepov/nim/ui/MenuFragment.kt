package ru.spbstu.shelepov.nim.ui


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.dialog_players_names.view.*
import kotlinx.android.synthetic.main.fragment_menu.*
import ru.spbstu.shelepov.nim.R
import ru.spbstu.shelepov.nim.model.GameDifficulty
import ru.spbstu.shelepov.nim.model.GameType

//Фрагмент меню. Просто кнопки с настройками игры, переходы дальше.
class MenuFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_menu
    private val bundle = Bundle()
    private lateinit var dialogView: View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_players_names, null)

        score_button.setOnClickListener {
            findNavController().navigate(R.id.scoreFragment)
        }

        hotseat_game_button.setOnClickListener {
            bundle.putString("game_type", GameType.HOTSEAT_GAME.toString())
            showDialog(GameType.HOTSEAT_GAME) { _, _ ->
                val firstPlayerName = dialogView.first_player_text.text.toString()
                val secondPlayerName = dialogView.second_player_text.text.toString()
                bundle.putString(
                    "first_player_name",
                    if (firstPlayerName == "") resources.getString(R.string.first_player) else firstPlayerName
                )
                bundle.putString(
                    "second_player_name",
                    if (secondPlayerName == "") resources.getString(R.string.second_player) else secondPlayerName
                )
                findNavController().navigate(R.id.gameFragment, bundle)
            }
        }

        computer_game_button.setOnClickListener {
            bundle.putString("game_type", GameType.COMPUTER_GAME.toString())
            showDialog(GameType.COMPUTER_GAME) { _, _ ->
                val firstPlayerName = dialogView.first_player_text.text.toString()
                bundle.putString(
                    "first_player_name",
                    if (firstPlayerName == "") resources.getString(R.string.first_player) else firstPlayerName
                )
                findNavController().navigate(R.id.gameFragment, bundle)
            }
        }

        difficulty_chip_group.setOnCheckedChangeListener { chipGroup, idSelected ->
            when (idSelected) {
                R.id.chip_easy -> {
                    bundle.putString("game_difficulty", GameDifficulty.EASY.toString())
                }
                R.id.chip_hard -> {
                    bundle.putString("game_difficulty", GameDifficulty.HARD.toString())
                }
            }
        }
    }

    private fun showDialog(
        gameType: GameType,
        onPositiveButtonClick: (DialogInterface, Int) -> Unit
    ) {
        val namesDialog = AlertDialog.Builder(requireContext())
        if (gameType == GameType.COMPUTER_GAME) dialogView.second_player_text.visibility = View.GONE
        if (dialogView.parent != null)
            (dialogView.parent as ViewGroup).removeView(dialogView)
        namesDialog.apply {
            setView(dialogView)
            setTitle(ru.spbstu.shelepov.nim.R.string.enter_players_names)
            setPositiveButton(R.string.start, onPositiveButtonClick)
            setNegativeButton(R.string.cancel) { _, _ -> }
            show()
        }
    }


}
