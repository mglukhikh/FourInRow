package ru.spbstu.fourinrow.core

import ru.spbstu.fourinrow.core.FourInRow.Chip.*
import java.util.*

class ComputerPlayer(private val field: FourInRow) {

    private val random = Random(Calendar.getInstance().timeInMillis)

    private val directions = arrayOf(FourInRow.Cell(0, 1), FourInRow.Cell(1, 0),
            FourInRow.Cell(1, 1), FourInRow.Cell(1, -1))

    private fun evalChipsInFour(number: Int) =
            when (number) {
                1 -> 1
                2 -> 10
                3 -> 500
                4 -> 10000
                else -> 0
            }

    private fun evaluation(side: FourInRow.Chip = YELLOW): Int {
        if (side == RED) return -evaluation(YELLOW)
        var result = random.nextInt(5) - 2
        with (field) {
            for (x in 0..width - 1) {
                for (y in 0..height - 1) {
                    val start = FourInRow.Cell(x, y)
                    for (dir in directions) {
                        val finish = start + dir * (winLength - 1)
                        if (!correct(finish)) continue
                        var yellows = 0
                        var reds = 0
                        fun chip(chip: FourInRow.Chip?) {
                            when (chip) {
                                YELLOW -> yellows++
                                RED -> reds++
                            }
                        }
                        chip(this[start])
                        var current = start
                        while (current != finish) {
                            current += dir
                            chip(this[current])
                        }
                        if (reds == 0)
                            result += evalChipsInFour(yellows)
                        if (yellows == 0)
                            result -= evalChipsInFour(reds)
                    }
                }
            }
        }
        return result
    }

    data class EvaluatedTurn(val turn: Int?, val evaluation: Int)

    fun bestTurn(depth: Int, lowerBound: Int = -1000000, upperBound: Int = 1000000): EvaluatedTurn {
        when (field.winner()) {
            field.turn -> return EvaluatedTurn(null, 10000 + depth)
            field.turn.opposite() -> return EvaluatedTurn(null, -10000 - depth)
            else -> {}
        }
        if (!field.hasFreeCells()) return EvaluatedTurn(null, 0)
        if (depth <= 0) return EvaluatedTurn(null, evaluation(field.turn))
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