package ru.spbstu.fourinrow.core

import ru.spbstu.fourinrow.core.FourInRow.Chip.*
import java.util.*

class ComputerPlayer(private val field: FourInRow) {

    private val random = Random(Calendar.getInstance().timeInMillis)

    private fun evaluation(side: FourInRow.Chip = YELLOW, depth: Int = 0): Int {
        if (side == RED) return -evaluation(depth = depth)
        when (field.winner()) {
            YELLOW -> return 10000 + depth
            RED    -> return -10000 - depth
            null   -> {}
        }
        if (!field.hasFreeCells()) return 0
        return random.nextInt(5) - 2
    }

    data class EvaluatedTurn(val turn: Int?, val evaluation: Int)

    fun bestTurn(depth: Int, lowerBound: Int = -1000000, upperBound: Int = 1000000): EvaluatedTurn {
        if (depth <= 0 || field.winner() != null || !field.hasFreeCells()) {
            return EvaluatedTurn(null, evaluation(field.turn, depth))
        }
        var lower = lowerBound
        var result = EvaluatedTurn(null, lower)
        for (turn in 0..field.width - 1) {
            if (field.makeTurn(turn) == null) continue
            val evaluation = -bestTurn(
                    depth = depth - 1,
                    lowerBound = -upperBound,
                    upperBound = -lowerBound
            ).evaluation
            field.takeTurnBack(turn)
            if (evaluation > lower) {
                lower = evaluation
                result = EvaluatedTurn(turn, lower)
                if (evaluation > upperBound) return result
            }
        }
        return result
    }
}