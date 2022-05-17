package com.example.drawingforkid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mDrawPath: CustomPath? = null

    /**
     * A Canvas is a drawing surface on which you can draw graphics.
     *
     * A Bitmap (or raster graphic) is a digital image composed of a matrix of dots
     *      (or pixels) that can be used to draw on a Canvas.
     */
    private var mCanvasBitmap: Bitmap? = null

    /**
     * Holds the style and color information for drawing geometries, text and bitmaps.
     */
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    init {
        setUpDrawing()
    }

    /**
     * This method initializes the attributes of the
     * ViewForDrawing class.
     */
    private fun setUpDrawing() {
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint = Paint()
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mDrawPaint!!.strokeWidth = mBrushSize
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * Draw the specified bitmap, with its top/left corner at (x,y), using the specified paint,
         * transformed by the current matrix.
         *
         * If the bitmap and canvas have different densities, this function will take care of
         * automatically scaling the bitmap to draw at the same density as the canvas.
         *
         * @param bitmap The bitmap to be drawn
         * @param left The position of the left side of the bitmap being drawn
         * @param top The position of the top side of the bitmap being drawn
         * @param paint The paint used to draw the bitmap (may be null)
         */
        mCanvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, mCanvasPaint) }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    /**
     * This method acts as an event listener when a touch
     * event is detected on the device.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                // Clear any lines and curves from the path, making it empty
                mDrawPath!!.reset()
                // Set the beginning of the next contour to the point (x,y).
                mDrawPath!!.moveTo(touchX, touchY)
            }
            // Add a line from the last point to the specified point (x,y).
            MotionEvent.ACTION_MOVE -> mDrawPath!!.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
//                canvas?.drawPath(mDrawPath!!, mDrawPaint!!)
//                mDrawPath!!.reset()
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path()

}

