package com.mosugu.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class ShapeConstructors(viewWidth : Int, context: Context,
                        showAlphaScale : Boolean, showMainColors : Boolean, mainColorsCount : Int) {
    private var alphaScaleHeight = 0F
    private var alphaScaleRect = RectF(0F, 0F, 0F, 0F)
    private lateinit var circleRect: RectF
    private var viewWidth = 0F
    private var mainColorsCount = 0
    private var mainColorsWidth = 0F
    private var leftMargin = 0F
    private var bottomMargin = 0F
    private var colorsArray : Array<String>
    private var mainColorRectF = RectF(0F, 0F, 0F, 0F)

    init {
        this.mainColorsCount = mainColorsCount
        this.viewWidth = viewWidth.toFloat()
        colorsArray = context.resources.getStringArray(R.array.colors_array)

        if (showMainColors) {
            mainColorsWidth = viewWidth / 14F
            leftMargin = 0F
        } else leftMargin = viewWidth / 14F
        if (showAlphaScale){
            alphaScaleHeight = viewWidth / 14F
            bottomMargin = 0F
        } else bottomMargin = viewWidth / 14F
    }

    fun getColorPickerRect(pickerDiameter : Float): RectF {
        val left = circleRect.centerX() - pickerDiameter / 2
        val top = circleRect.centerY() - pickerDiameter / 2
        val right = circleRect.centerX() + pickerDiameter
        val bottom = circleRect.centerY() + pickerDiameter
        return RectF(left, top, right, bottom)
    }

    fun getAlphaScaleRect(): RectF {
        alphaScaleRect = RectF(0F,
                                viewWidth - alphaScaleHeight * 1.3F,
                                    viewWidth,
                             viewWidth - alphaScaleHeight * 0.3F)
        return alphaScaleRect
    }

    fun getColorCircleRect(): RectF {
        circleRect = if (mainColorsWidth == 0F && alphaScaleHeight == 0F) {
                        RectF(0F, 0F, viewWidth, viewWidth)
                    } else  RectF(leftMargin,
                                0F + bottomMargin,
                            viewWidth - mainColorsWidth * 2 - leftMargin,
                          viewWidth - alphaScaleHeight * 2 - bottomMargin )
        return circleRect
    }

    fun getAlphaPickerRect() : RectF {
        return  RectF((alphaScaleRect.right - alphaScaleHeight * 0.4F),
                        (alphaScaleRect.top - alphaScaleHeight * 0.2F),
                        alphaScaleRect.right,
                    alphaScaleRect.bottom + alphaScaleHeight * 0.2F)
    }

    fun getMainColorRectF(rectByOrder : Int) : RectF {
        val mainColorHeight = viewWidth - bottomMargin * 2F - alphaScaleHeight * 2F
        val oneBlockHeight = mainColorHeight / mainColorsCount
        mainColorRectF = RectF(viewWidth - mainColorsWidth,
                                0.01F + bottomMargin + rectByOrder * oneBlockHeight,
                                viewWidth,
                                bottomMargin + oneBlockHeight + rectByOrder * oneBlockHeight)

        return mainColorRectF
    }

    fun getPaint(paintByOrder: Int): Paint {
        return Paint().apply {color = Color.parseColor(colorsArray[paintByOrder])}
    }

}