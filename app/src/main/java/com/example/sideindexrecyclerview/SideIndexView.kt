package com.example.sideindexrecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SideIndexView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val indexLetters = listOf("#") + ('A'..'Z').map { it.toString() }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private var letterHeight = 0f
    private var listener: ((String) -> Unit)? = null

    private var floatingLetter: String? = null
    private var floatingTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 100f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    fun setOnLetterClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        letterHeight = height / indexLetters.size.toFloat()

        //  Draw A-Z index and only highlight the touched letter
        for (i in indexLetters.indices) {
            val x = width - 50f  // Right-aligned sidebar letters
            val y =  (i + 1).toFloat() * letterHeight

            if (floatingLetter == indexLetters[i]) {

                textPaint.apply {
                    color = Color.BLUE // Highlight color
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 50f
                }
            } else {
                // Normal sidebar letters
                textPaint.apply {
                    color = Color.BLACK
                    typeface = Typeface.DEFAULT
                    textSize = 40f
                }
            }

            // Draw index letter
            canvas.drawText(indexLetters[i], x, y, textPaint)
        }

        // Draw floating letter in **center of the screen**
        floatingLetter?.let {
            val centerX = width / 2  // Always center horizontally
            val centerY = height / 2f  // Always center vertically
            val circleRadius = 100f

            // Floating letter background
            val circlePaint = Paint().apply {
                color = Color.BLACK
                alpha = 200
                isAntiAlias = true
            }

            // Draw floating lett
            // er in center WITHOUT affecting sidebar
            canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
            canvas.drawText(it, centerX, centerY + (floatingTextPaint.textSize / 3), floatingTextPaint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val index = (event.y / letterHeight).toInt()
                if (index in indexLetters.indices) {
                    floatingLetter = indexLetters[index]
                    listener?.invoke(indexLetters[index])
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                floatingLetter = null
                invalidate()
            }
        }
        return true
    }
}
