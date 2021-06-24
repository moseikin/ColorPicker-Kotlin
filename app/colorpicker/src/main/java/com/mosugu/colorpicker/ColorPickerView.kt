package com.mosugu.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.IllegalArgumentException
import kotlin.math.*


class ColorPickerView (context: Context) : View (context) {

    constructor(context: Context, attributeSet: AttributeSet): this(context) {
        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.ColorPickerView)
        showAlphaScale = attributes.getBoolean(R.styleable.ColorPickerView_show_alpha_scale, true)
        showMainColors = attributes.getBoolean(R.styleable.ColorPickerView_show_main_colors, true)
        attributes.recycle()
    }
    private var paint = Paint()
    private var alphaScalePaint = Paint()
    private var showAlphaScale: Boolean = true
    private var showMainColors: Boolean = true
    private var bitmapPicker: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.picker)
    private var bitmapCircle: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.color_circle)
    private var bitmapAlphaPicker = BitmapFactory.decodeResource(resources, R.drawable.alpha_picker)
    private var bitmapAlphaScaleBackground: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.alpha_scale)
    private lateinit var bitmapAlphaScale: Bitmap
    private lateinit var bitmap: Bitmap
    private val mainColorsCount = 8
    private var colorsRectArray = arrayOfNulls<RectF>(mainColorsCount)
    private var colorsPaintArray = arrayOfNulls<Paint>(mainColorsCount)
    private lateinit var rectCircle: RectF
    private lateinit var rectColorPicker: RectF
    private var rectAlphaScale: RectF = RectF(0F,0F,0F, 0F)
    private lateinit var rectAlphaPicker: RectF
    private var circleRadius = 0F
    private var pickerRadius = 0F
    private var colorARGB = Color.WHITE
    private var red = 255
    private var green = 255
    private var blue = 255
    private var alpha = 255
    private var lastPressedFigure = 0
    private var colorTransparent = ContextCompat.getColor(context, R.color.transparent)
    private lateinit var linearGradient : LinearGradient
    private var gradientColorsArray = IntArray(2)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // getting view width. This value will be assign to view height. So view has square form anyway
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val minDimension = min(widthSize, heightSize)
        setMeasuredDimension(minDimension, minDimension)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShapes(w)
    }

    private fun initShapes(w : Int){
        val shapeConstructors = ShapeConstructors(w, context, showAlphaScale, showMainColors, mainColorsCount)
        if(showAlphaScale) {
            paintAlphaScale(colorARGB)
            rectAlphaScale = shapeConstructors.getAlphaScaleRect()
            rectAlphaPicker = shapeConstructors.getAlphaPickerRect()
            bitmapAlphaScale = Bitmap.createBitmap(rectAlphaScale.width().toInt(),
            rectAlphaScale.height().toInt(), Bitmap.Config.ARGB_8888)
        }
        if (showMainColors) {
            for (i in 0 until mainColorsCount) {
                colorsRectArray[i] = shapeConstructors.getMainColorRectF(i)
                colorsPaintArray[i] = shapeConstructors.getPaint(i)
            }

        }
        rectCircle = shapeConstructors.getColorCircleRect()

        // reducing with 2 allows picker run farther from the palette edge to avoid catching white pixels
        circleRadius = rectCircle.width() / 2 - 2

        val pickerDiameter = (if (rectCircle.width() / 20 < 10) 10 else rectCircle.width() / 20).toFloat()
        rectColorPicker = shapeConstructors.getColorPickerRect(pickerDiameter)
        pickerRadius = rectColorPicker.width() / 2
        bitmap = Bitmap.createBitmap(width, height , Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmapCircle, null, rectCircle, paint)
        canvas?.drawBitmap(bitmapPicker, null, rectColorPicker, paint)
        if (showAlphaScale) {
            canvas?.drawBitmap(bitmapAlphaScaleBackground, null, rectAlphaScale, alphaScalePaint)
            canvas?.drawRect(rectAlphaScale, alphaScalePaint)
            canvas?.drawBitmap(bitmapAlphaPicker, null, rectAlphaPicker, paint)
        }

        if(showMainColors) {
            for ( i in colorsRectArray.indices ) {
                canvas?.drawRect(colorsRectArray[i]!!, colorsPaintArray[i]!!)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE) {
            if (event.x >= rectCircle.left && event.x <= rectCircle.right &&
                event.y >= rectCircle.top && event.y <= rectCircle.bottom || lastPressedFigure == 1) {
                lastPressedFigure = 1
                placeColorPicker(event)
            } else if (event.x >= rectAlphaScale.left - 50 && event.x <= rectAlphaScale.right + 50 &&
                event.y >= rectAlphaScale.top && event.y <= rectAlphaScale.bottom || lastPressedFigure == 2) {
                lastPressedFigure = 2
                placeAlphaPicker(event)
            } else if (event.x >= colorsRectArray[0]?.left!! && event.x <= colorsRectArray[0]?.right!! &&
                        event.y >= colorsRectArray[0]?.top!! && event.y <= colorsRectArray[mainColorsCount - 1]?.bottom!!) {
                getPixelColor(event.x, event.y)
            }
            // redrawing canvas to apple changes
            invalidate()
        } else lastPressedFigure = 0
        return true
    }


    private fun placeColorPicker(event: MotionEvent) {
        var dX = 0F
        var dY = 0F

        // distance is the hypotenuse of triangle between dots: palette center, color picker position and dot of
        // intersection of horizontal and vertical lines, conducted from those dots
        // distance must be <= palette radius
        val distance = hypot((event.x - circleRadius - rectCircle.left),
            (event.y - circleRadius - rectCircle.top))

        if (distance < circleRadius) {
            dX = event.x - pickerRadius
            dY = event.y - pickerRadius

        } else if (distance >= circleRadius) {
    // If colorPicker is going to leave palette bounds, need to find dot on palette circle to place picker and limit distancing.
    // Tangent of angle between leg and hypotenuse is opposite leg relation to adjacent leg. This value arctangent is angle.
    // Each leg (x and y coordinates of picker) will be found as composition of hypotenuse (palette radius) and angle cosine / sinus
    // with correction by palette position in parent and picker radius

            val angleRadian = atan2(event.y - rectCircle.centerY(), event.x - rectCircle.centerX())
            dX = (circleRadius * (cos(angleRadian)) + rectCircle.centerX() - pickerRadius)
            dY = (circleRadius * (sin(angleRadian)) + rectCircle.centerY() - pickerRadius)
        }
        // place picker to new coordinates
        rectColorPicker.offsetTo(dX, dY)

        getPixelColor(dX, dY)
    }

    private fun getPixelColor(coordinateX : Float, coordinateY : Float) {
        // adding pickerRadius to each coordinate means, that we want to pick pixel from picker center, not it left top angle
        val pickerCenterX = (coordinateX + pickerRadius).toInt()
        val pickerCenterY = (coordinateY + pickerRadius).toInt()
        try {
            val pixel = bitmap.getPixel(pickerCenterX, pickerCenterY)
            red = Color.red(pixel)
            green = Color.green(pixel)
            blue = Color.blue(pixel)
            colorARGB = Color.argb(alpha, red, green, blue)
            paintAlphaScale(colorARGB)
            Data.setColorArgb(colorARGB)         // passing color to singleton to allow outer classes get it
        } catch (e: IllegalArgumentException) {
            // occurs if x or y out of bitmap bounds
        }

    }

    private fun placeAlphaPicker(event: MotionEvent) {
        // coerceIn fun limits alpha picker with alpha scale bounds
        val dX = event.x.coerceIn(rectAlphaScale.left, rectAlphaScale.right - rectAlphaPicker.width())
        rectAlphaPicker.offsetTo(dX, rectAlphaPicker.top)

        // relativePosition is the relation between distance from scale left and picker and scale width.
        // I.e. it is the relative value alpha must be reduced
        val relativePosition = (rectAlphaPicker.left - rectAlphaScale.left) / (rectAlphaScale.width() - rectAlphaPicker.width())
        alpha = (255 * relativePosition).toInt()
        colorARGB = Color.argb(alpha, red, green, blue)

        Data.setColorArgb(colorARGB)
    }

    private fun paintAlphaScale(colorArgb : Int){
        gradientColorsArray[0] = colorTransparent
        gradientColorsArray[1] = colorArgb
        linearGradient =
            LinearGradient(rectAlphaScale.left, rectAlphaScale.top, rectAlphaScale.right, rectAlphaScale.bottom,
                            gradientColorsArray, null, Shader.TileMode.MIRROR)
        alphaScalePaint.shader = linearGradient
    }

}