package com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

import android.content.Context
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import androidx.core.graphics.ColorUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Components.ColorComponent
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Components.HueComponent
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Components.LightnessComponent
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Components.SaturationComponent
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Listeners.OnColorSelectionListener


open class HSLColorPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val metrics = Metrics(density = resources.displayMetrics.density)
    private val paints = Paints()

    private val hueComponent: ColorComponent
    private val saturationComponent: ColorComponent
    private val lightnessComponent: ColorComponent

    private val hueRadiusOffset: Float
    private val saturationRadiusOffset: Float
    private val lightnessRadiusOffset: Float

    init {
        val globalArcWidth = dp(5f)
        val globalStrokeWidth = 0f
        val globalIndicatorRadius = dp(15f)
        val globalIndicatorStrokeWidth = dp(2f)
        val globalStrokeColor = 0
        val globalIndicatorStrokeColor = 0
        val globalArcLength = 0f
        val globalRadiusOffset = 0f

        val saturationArcLength = 155f
        val saturationStartAngle = 100f

        saturationComponent = SaturationComponent(metrics, paints, saturationArcLength, saturationStartAngle)

        val lightnessArcLength = 155f
        val lightnessStartAngle = 280f

        lightnessComponent = LightnessComponent(metrics, paints, lightnessArcLength, lightnessStartAngle)

        val hueArcLength = 360f
        val hueStartAngle = 0f

        hueComponent = HueComponent(metrics, paints, hueArcLength, hueStartAngle)

        hueComponent.strokeWidth = globalArcWidth
        hueComponent.borderWidth = globalStrokeWidth
        hueComponent.indicatorStrokeWidth = globalIndicatorStrokeWidth
        hueComponent.indicatorStrokeColor = globalIndicatorStrokeColor
        hueComponent.strokeColor = globalStrokeColor
        hueComponent.indicatorRadius = globalIndicatorRadius

        saturationComponent.strokeWidth = globalArcWidth
        saturationComponent.borderWidth = globalStrokeWidth
        saturationComponent.indicatorStrokeWidth = globalIndicatorStrokeWidth
        saturationComponent.indicatorStrokeColor = globalIndicatorStrokeColor
        saturationComponent.strokeColor = globalStrokeColor
        saturationComponent.indicatorRadius = globalIndicatorRadius

        lightnessComponent.strokeWidth = globalArcWidth
        lightnessComponent.borderWidth = globalStrokeWidth
        lightnessComponent.indicatorStrokeWidth = globalIndicatorStrokeWidth
        lightnessComponent.indicatorStrokeColor = globalIndicatorStrokeColor
        lightnessComponent.strokeColor = globalStrokeColor
        lightnessComponent.indicatorRadius = globalIndicatorRadius

        hueRadiusOffset = dp(1f)
        saturationRadiusOffset = dp(25f)
        lightnessRadiusOffset = dp(25f)
    }

    override fun onDraw(canvas: Canvas) {
        hueComponent.drawComponent(canvas)
        saturationComponent.drawComponent(canvas)
        lightnessComponent.drawComponent(canvas)
    }

    override fun onSizeChanged(width: Int, height: Int, oldW: Int, oldH: Int) {
        val minimumSize = if (width > height) height else width
        val padding = (paddingLeft + paddingRight + paddingTop + paddingBottom) / 4f
        val outerRadius = minimumSize.toFloat() / 2f - padding

        hueComponent.setRadius(outerRadius, hueRadiusOffset)
        saturationComponent.setRadius(outerRadius, saturationRadiusOffset)
        lightnessComponent.setRadius(outerRadius, lightnessRadiusOffset)

        metrics.centerX = width / 2f
        metrics.centerY = height / 2f

        hueComponent.updateComponent(hueComponent.angle)
        saturationComponent.updateComponent(saturationComponent.angle)
        lightnessComponent.updateComponent(lightnessComponent.angle)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var isTouched = true
        if (!hueComponent.onTouchEvent(event)) {
            if (!saturationComponent.onTouchEvent(event)) {
                isTouched = lightnessComponent.onTouchEvent(event)
            }
        }
        invalidate()
        return isTouched
    }

    fun setColorSelectionListener(listener: OnColorSelectionListener) {
        hueComponent.setColorSelectionListener(listener)
        saturationComponent.setColorSelectionListener(listener)
        lightnessComponent.setColorSelectionListener(listener)
    }

    open fun setColor(color: Int) {
        with(metrics) {
            ColorUtils.colorToHSL(color, hsl)
            hueComponent.updateAngle(hsl[0])
            saturationComponent.updateAngle(hsl[1])
            lightnessComponent.updateAngle(hsl[2])
        }
        invalidate()
    }

    private fun dp(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = super.onSaveInstanceState()
        val savedState = SavedState(bundle!!)
        savedState.color = ColorUtils.HSLToColor(metrics.hsl)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setColor(state.color)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var color: Int = 0

        constructor(bundle: Parcelable) : super(bundle)

        private constructor(parcel: Parcel) : super(parcel) {
            color = parcel.readInt()
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(color)
        }
    }
}