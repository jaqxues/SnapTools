package com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Components

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

import androidx.core.graphics.ColorUtils
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Metrics
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Paints

internal class HueComponent(metrics: Metrics, paints: Paints, override val arcLength: Float, override val arcStartAngle: Float) : ArcComponent(metrics, paints) {
    override val range: Float = 360f
    override val hslIndex: Int = 0
    override val NO_OF_COLORS = 360
    override val colors = IntArray(NO_OF_COLORS)
    override val colorPosition = FloatArray(NO_OF_COLORS)

    init {
        angle = (arcStartAngle + arcLength / 2f).toDouble()
    }

    override fun getColorArray(hsl: FloatArray): IntArray {
        hsl[1] = 1f
        hsl[2] = 0.5f
        for (i in 0..NO_OF_COLORS - 1) {
            hsl[hslIndex] = i.toFloat()
            colors[i] = ColorUtils.HSLToColor(hsl)
        }
        return colors
    }

}