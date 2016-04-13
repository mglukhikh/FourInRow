package ru.spbstu.fourinrow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.*
import ru.spbstu.fourinrow.core.FourInRow
import java.util.*

class FieldActivity : AppCompatActivity() {

    lateinit var ui : FieldActivityUi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = FieldActivityUi()
        ui.setContentView(this)
    }

    override fun onStart() {
        super.onStart()
        ui.refresh()
    }
}

class FieldActivityUi : AnkoComponent<FieldActivity> {

    private val customStyle = { v: Any ->
        when (v) {
            is Button -> {
                v.maxWidth = 2
            }
        }
    }
    private val field = FourInRow()

    private var over = false

    private lateinit var message: TextView

    private val buttons = HashMap<FourInRow.Cell, Button>()

    private fun FourInRow.Chip.text() = toString().toLowerCase()

    internal fun refresh() {
        for ((cell, button) in buttons) {
            val chip = field[cell]
            button.backgroundColor = when (chip) {
                FourInRow.Chip.YELLOW -> 0xffffff00L
                FourInRow.Chip.RED -> 0xffff0000L
                null -> 0xff808080L
            }.toInt()
        }
        val winner = field.winner()
        if (winner != null) {
            over = true
            message.text = "${winner.text()} wins!"
        }
        else if (!field.hasFreeCells()) {
            over = true
            message.text = "Draw!"
        }
        else {
            message.text = "Make your turn, ${field.turn.text()}"
        }
    }

    override fun createView(ui: AnkoContext<FieldActivity>) = with(ui) {
        verticalLayout {
            message = textView("Make your turn, yellow")
            gridLayout {
                columnCount = field.width
                rowCount = field.height
                useDefaultMargins = true

                for (row in 0..rowCount - 1) {
                    for (column in 0..columnCount - 1) {
                        val b = button {
                            onClick {
                                if (!over) {
                                    field.makeTurn(column)
                                }
                                else {
                                    field.clear()
                                }
                                refresh()
                            }
                        }
                        b.maxWidth = 2
                        buttons[FourInRow.Cell(column, field.height - row - 1)] = b
                    }
                }
            }.style(customStyle)
        }
    }
}
