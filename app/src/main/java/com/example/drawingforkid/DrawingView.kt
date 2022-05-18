package com.example.drawingforkid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    /**
     * Variable of the CustomPath internal inner class
     */
    private var mDrawPath: CustomPath? = null
    private var mPaths = ArrayList<CustomPath>()

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

    /**
     * A variable for canvas which will be initialized later and used.
     *
     * The Canvas class holds the "draw" calls. To draw something, you need 4 basic components:
     * - A Bitmap to hold the pixels,
     * - A Canvas to host the draw calls (writing into the bitmap),
     * - A drawing primitive (e.g. Rect, Path, text, Bitmap),
     * - A paint (to describe the colors and styles for the drawing)
     */
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

        mDrawPaint!!.style = Paint.Style.STROKE         // To use STROKE style
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND      // To use ROUND stroke join
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND        // To use ROUND stroke cap

        // Paint flag that enables dithering when blitting
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
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

        /* Draw the specified bitmap, with its top/left corner at (x,y),
           using the specified paint, transformed by the current matrix */
        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mCanvasPaint)
        }

        for (path in mPaths) {
            mDrawPaint?.strokeWidth = path.brushThickness
            mDrawPaint?.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

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
                // Add when the stroke is drawn to canvas, and added in the path arraylist
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    /**
     * This method is called when either the brush or the eraser
     * sizes are to be changed. This method sets the brush/eraser
     * sizes to the new values depending on user selection.
     */
    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    /**
     * This function is called when the user desires a color change.
     * This functions sets the color of a store to selected color and able to draw on view using that color.
     *
     * @param newColor
     */
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path()

}

