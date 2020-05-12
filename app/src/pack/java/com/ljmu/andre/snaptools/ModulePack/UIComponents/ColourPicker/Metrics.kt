package com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

import android.graphics.Paint
import androidx.core.graphics.ColorUtils

data class Metrics(var centerX: Float = 0f, var centerY: Float = 0f, var hsl: FloatArray = floatArrayOf(0f, 1f, 0.5f), val density: Float)

data class Paints(val shaderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG), val indicatorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG))

fun Metrics.color() = ColorUtils.HSLToColor(hsl)