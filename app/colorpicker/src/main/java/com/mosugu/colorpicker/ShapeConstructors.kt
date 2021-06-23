package com.mosugu.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.min

class ShapeConstructors(viewWidth : Int, viewHeight : Int, context: Context,
                        showAlphaScale : Boolean, showMainColors : Boolean, mainColorsCount : Int) {
    private var alphaScaleHeight = 0F
    private var alphaScaleRect = RectF(0F, 0F, 0F, 0F)
    private lateinit var circleRect: RectF
    private var minDensity = 0F
    private var viewWidth = 0F
    private var viewHeight : Float
    private var viewCenterX : Float
    private var viewCenterY : Float
    private var mainColorsCount = 0
    private var mainColorsWidth = 0F
    private var leftMargin = 0F
    private var bottomMargin = 0F
    private var colorsArray : Array<String>
    private var mainColorRectF = RectF(0F, 0F, 0F, 0F)

    init {
        this.mainColorsCount = mainColorsCount
        this.viewWidth = viewWidth.toFloat()
        this.viewHeight = viewHeight.toFloat()
        viewCenterX = (viewWidth / 2).toFloat()
        viewCenterY = (viewHeight / 2).toFloat()
        colorsArray = context.resources.getStringArray(R.array.colors_array)
        minDensity = min(viewWidth, viewHeight).toFloat()
        if (showMainColors) {
            mainColorsWidth = minDensity / 14
            leftMargin = 0F
        } else leftMargin = minDensity / 14
        if (showAlphaScale){
            alphaScaleHeight = minDensity / 14
            bottomMargin = 0F
        } else bottomMargin = minDensity / 14


    }

    fun getColorPickerRect(pickerDiameter : Float): RectF {
        val left = circleRect.centerX() - pickerDiameter / 2
        val top = circleRect.centerY() - pickerDiameter / 2
        val right = circleRect.centerX() + pickerDiameter
        val bottom = circleRect.centerY() + pickerDiameter
        return RectF(left, top, right, bottom)
    }

    fun getAlphaScaleRect(): RectF {
        alphaScaleRect = RectF(viewCenterX - minDensity /2,
                                minDensity - alphaScaleHeight * 1.3F,
                            viewCenterX + minDensity / 2,
                                minDensity - alphaScaleHeight * 0.3F)
        return alphaScaleRect
    }

    fun getColorCircleRect(): RectF {
        circleRect = if (mainColorsWidth == 0F && alphaScaleHeight == 0F) {
                        RectF(viewCenterX - minDensity / 2,
                            0F,
                            viewCenterX + minDensity / 2,
                            minDensity)
                    } else  RectF(viewCenterX - minDensity / 2 + leftMargin,
                                0F + bottomMargin,
                            viewCenterX + minDensity / 2 - mainColorsWidth * 2 - leftMargin,
                            minDensity - alphaScaleHeight * 2 - bottomMargin )
        return circleRect
    }

    fun getAlphaPickerRect() : RectF {
        return  RectF((alphaScaleRect.right - alphaScaleHeight * 0.4F),
                        (alphaScaleRect.top - alphaScaleHeight * 0.2F),
                        alphaScaleRect.right,
                    alphaScaleRect.bottom + alphaScaleHeight * 0.2F)
    }

    fun getMainColorRectF(rectByOrder : Int) : RectF {
        val mainColorHeight = minDensity - bottomMargin * 2F - alphaScaleHeight * 2F
        val oneBlockHeight = mainColorHeight / mainColorsCount
        mainColorRectF = RectF(viewCenterX + minDensity / 2 - mainColorsWidth,
                                0.01F + bottomMargin + rectByOrder * oneBlockHeight,
                                viewCenterX + minDensity / 2,
                                bottomMargin + oneBlockHeight + rectByOrder * oneBlockHeight)

        return mainColorRectF
    }

    fun getPaint(paintByOrder: Int): Paint {
        return Paint().apply {color = Color.parseColor(colorsArray[paintByOrder])}
    }

}