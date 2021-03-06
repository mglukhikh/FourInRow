package ru.spbstu.fourinrow.core

import java.util.*

class FourInRow(val width: Int = 7, val height: Int = 6, val winLength: Int = 4) {

    enum class Chip {
        YELLOW,
        RED;

        fun opposite() = if (this == YELLOW) RED else YELLOW
    }

    data class Cell(internal val x: Int, internal val y: Int) {

        operator fun plus(arg: Cell) = Cell(x + arg.x, y + arg.y)

        operator fun times(arg: Int) = Cell(x * arg, y * arg)
    }

    private val chips = HashMap<Cell, Chip>()

    var turn = Chip.YELLOW
        internal set

    fun clear() {
        chips.clear()
        turn = Chip.YELLOW
    }

    operator fun get(x: Int, y: Int) = get(Cell(x, y))

    operator fun get(cell: Cell) = chips[cell]

    internal operator fun set(x: Int, y: Int, chip: Chip?) {
        if (chip != null) {
            chips.put(Cell(x, y), chip)
        }
        else {
            chips.remove(Cell(x, y))
        }
    }

    fun makeTurn(x: Int): Cell? {
        if (x < 0 || x >= width) return null
        for (y in 0..height - 1) {
            val cell = Cell(x, y)
            if (this[cell] == null) {
                chips[cell] = turn
                turn = turn.opposite()
                return cell
            }
        }
        return null
    }

    fun takeTurnBack(x: Int): Cell? {
        if (x < 0 || x >= width) return null
        for (y in 0..height) {
            if (y == height || this[x, y] == null) {
                if (y == 0) return null
                val cell = Cell(x, y - 1)
                chips.remove(cell)
                turn = turn.opposite()
                return cell
            }
        }
        return null
    }

    fun hasFreeCells(): Boolean {
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                if (this[x, y] == null) return true
            }
        }
        return false
    }

    private val directions = arrayOf(Cell(0, 1), Cell(1, 0), Cell(1, 1), Cell(1, -1))

    fun correct(cell: Cell) = cell.x >= 0 && cell.x < width && cell.y >= 0 && cell.y < height

    fun winner(): Chip? {
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                val cell = Cell(x, y)
                val start = this[cell] ?: continue
                for (dir in directions) {
                    var length = 0
                    var current = cell
                    while (++length < winLength) {
                        current += dir
                        if (!correct(current)) break
                        if (this[current] != start) break
                    }
                    if (length == winLength) {
                        return start
                    }
                }
            }
        }
        return null
    }
}
