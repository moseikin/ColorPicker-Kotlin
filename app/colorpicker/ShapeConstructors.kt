package com.mosugu.colorpicker

import android.graphics.RectF
import kotlin.math.min

class ShapeConstructors(viewWidth : Int, viewHeight : Int) {
    private var alphaScaleHeight = 0F
    private lateinit var alphaScaleRect: RectF
    private lateinit var circleRect: RectF
    private var minDensity = 0F
    private var viewWidth = 0F
    private var viewHeight : Float
    private var viewCenterX : Float
    private var viewCenterY : Float

    init {
        this.viewWidth = viewWidth.toFloat()
        this.viewHeight = viewHeight.toFloat()
        viewCenterX = (viewWidth / 2).toFloat()
        viewCenterY = (viewHeight / 2).toFloat()
    }

    fun getColorPickerRect(pickerDiameter : Float): RectF {
        val left = circleRect.centerX() - pickerDiameter / 2
        val top = circleRect.centerY() - pickerDiameter / 2
        val right = circleRect.centerX() + pickerDiameter
        val bottom = circleRect.centerY() + pickerDiameter
        return RectF(left, top, right, bottom)
    }

    fun getAlphaScaleRect(): RectF {
        minDensity = min(viewWidth, viewHeight)
        alphaScaleHeight = minDensity / 14
        alphaScaleRect = RectF(viewCenterX - minDensity /2 + alphaScaleHeight,
                                minDensity - alphaScaleHeight * 1.3.toFloat(),
                            viewCenterX + minDensity / 2 - alphaScaleHeight,
                                minDensity - alphaScaleHeight * 0.3.toFloat())
        return alphaScaleRect
    }

    fun getColorCircleRect(): RectF {
        circleRect = if (minDensity == 0F) {
            when{
                viewWidth > viewHeight -> {
                    RectF(viewCenterX - viewCenterY + alphaScaleHeight, 0F,
                        viewCenterX + viewCenterY - alphaScaleHeight, viewHeight - alphaScaleHeight * 2)
                }
                viewHeight > viewWidth -> {
                    RectF(0 + alphaScaleHeight,
                            viewCenterY - viewCenterX,
                           viewWidth - alphaScaleHeight,
                         viewCenterY + viewCenterX - alphaScaleHeight * 2)
                }
                else ->{
                    RectF(0 + alphaScaleHeight, 0F,
                        viewWidth - alphaScaleHeight,
                        viewHeight - alphaScaleHeight * 2)
                }
            }
        } else {
            RectF(viewCenterX - minDensity / 2 + alphaScaleHeight, 0F,
                viewCenterX + minDensity / 2 - alphaScaleHeight, minDensity - alphaScaleHeight * 2 )
        }
        return circleRect
    }

    fun getAlphaPickerRect() : RectF {
        return  RectF((alphaScaleRect.right - alphaScaleHeight * 0.4).toFloat(),
                        (alphaScaleRect.top - alphaScaleHeight * 0.2).toFloat(),
                        alphaScaleRect.right,
                    alphaScaleRect.bottom + alphaScaleHeight * 0.2.toFloat())
    }

}