package ru.spbstu.fourinrow

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import ru.spbstu.fourinrow.core.FourInRow
import ru.spbstu.fourinrow.core.FourInRow.Chip.RED
import ru.spbstu.fourinrow.core.FourInRow.Chip.YELLOW
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

    private val field = FourInRow()

    private var over = false

    private lateinit var message: TextView

    private val buttons = HashMap<FourInRow.Cell, Button>()

    private fun FourInRow.Chip.text() = toString().toLowerCase()

    internal fun refresh() {
        for ((cell, button) in buttons) {
            val chip = field[cell]
            button.backgroundColor = when (chip) {
                YELLOW -> Color.YELLOW
                RED -> Color.RED
                null -> Color.GRAY
            }
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
            message = textView()
            linearLayout {
                orientation = LinearLayout.VERTICAL
                weightSum = 1.0f
                for (row in 0..field.height - 1) {
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        weightSum = 1.0f
                        for (column in 0..field.width - 1) {
                            val b = button {
                                onClick {
                                    if (!over) {
                                        field.makeTurn(column)
                                    } else {
                                        field.clear()
                                    }
                                    refresh()
                                }
                                lparams {
                                    bottomMargin = dip(1)
                                    topMargin = dip(1)
                                    leftMargin = dip(1)
                                    rightMargin = dip(1)
                                    weight = 1.0f / field.width
                                }
                            }
                            buttons[FourInRow.Cell(column, field.height - row - 1)] = b
                        }
                    }
                }
            }
        }
    }
}
