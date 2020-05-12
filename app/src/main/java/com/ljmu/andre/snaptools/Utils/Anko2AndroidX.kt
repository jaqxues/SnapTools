package com.ljmu.andre.snaptools.Utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewManager
import androidx.appcompat.widget.SwitchCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView


/**
 * Error Layer for the following reasons
 *
  * SnapTools uses Anko to create Views at Runtime
  * Anko uses android.support packages
  * Jetifier converts these automatically --> Code works even if it does show errors
  * Jetpack, Anko alternative, is not quite ready yet
 *
 * This just is just copy/pasted code and substituting with androidx. It would compile correctly, but still show as error
 */

open class _ViewPager(ctx: Context): ViewPager(ctx) {

    inline fun <T: View> T.lparams(init: LayoutParams.() -> Unit): T {
        val layoutParams = LayoutParams()
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(): T {
        val layoutParams = LayoutParams()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            context: Context?,
            attrs: AttributeSet?,
            init: LayoutParams.() -> Unit
    ): T {
        val layoutParams = LayoutParams(context!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            context: Context?,
            attrs: AttributeSet?
    ): T {
        val layoutParams = LayoutParams(context!!, attrs!!)
        this@lparams.layoutParams = layoutParams
        return this
    }
}

inline fun ViewManager.viewPagerX(theme: Int = 0, init: (@AnkoViewDslMarker _ViewPager).() -> Unit): _ViewPager {
    return ankoView({ctx -> _ViewPager(ctx)}, theme, init)
}

inline fun ViewManager.themedSwitchCompatX(theme: Int = 0, init: (@AnkoViewDslMarker SwitchCompat).() -> Unit): SwitchCompat {
    return ankoView({ctx -> SwitchCompat(ctx)}, theme, init)
}

inline fun ViewManager.swipeRefreshLayoutX(theme: Int = 0, init: (@AnkoViewDslMarker SwipeRefreshLayout).() -> Unit): SwipeRefreshLayout {
    return ankoView({ctx -> SwipeRefreshLayout(ctx)}, theme, init)
}