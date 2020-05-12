package com.ljmu.andre.snaptools.Fragments.ViewProviders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ljmu.andre.snaptools.Fragments.ViewProviders.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.UIComponents.Adapters.ListedViewPageAdapter
import com.ljmu.andre.snaptools.Utils.AnimationUtils
import com.ljmu.andre.snaptools.Utils.Callable
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.viewPager

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */


class FeaturesViewProvider(
        val activity: Activity, private val basicViewCallable: Callable<ViewGroup>, private val premiumViewCallable: Callable<ViewGroup>
) {

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(): T =
            activity.UI {
                verticalLayout {
                    val tabStrip = customTabStrip {
                        id = ResourceUtils.getIdFromString("tab_strip")
                    }


                    verticalLayout {
                        horizontalPadding = 32.toDp()
                        topPadding = 16.toDp()

                        val viewPager: ViewPager = viewPager {
                            id = ResourceUtils.getIdFromString("view_pager")
                        }.lparams(width = matchParent, height = matchParent)

                        setupPages(viewPager)

                        tabStrip.setupWithViewPager(viewPager)
                    }.lparams(height = matchParent)
                }
            }.view as T

    private fun setupPages(viewPager: ViewPager) {
        val viewList = ArrayList<Pair<String, View>>()

        viewList.add(Pair.create("Basic", initBasicFeatures()))
        viewList.add(Pair.create("Premium", initPremiumFeatures()))

        viewPager.adapter = ListedViewPageAdapter(
                viewList
        )
    }


    private fun <T : ViewGroup> initBasicFeatures(): T =
            activity.UI {
                frameLayout {
                    lparams(matchParent, matchParent)
                    val mainContainer = this

                    swipeRefreshLayout {
                        id = "basic_refresh".toId()
                        scrollView {
                            id = "scrollview_basic".toId()

                            verticalLayout {
                                id = "content_basic".toId()

                                basicViewCallable.call(mainContainer)
                            }.lparams(matchParent, wrapContent)
                        }
                    }.lparams(matchParent, matchParent)
                }
            }.view as T


    private fun <T : ViewGroup> initPremiumFeatures(): T =
            activity.UI {
                frameLayout {
                    lparams(matchParent, matchParent)
                    val mainContainer = this

                    swipeRefreshLayout {
                        id = "premium_refresh".toId()

                        scrollView {
                            lparams(matchParent, matchParent)
                            id = "scrollview_premium".toId()

                            verticalLayout {
                                id = "content_premium".toId()

                                premiumViewCallable.call(mainContainer)
                            }.lparams(matchParent, matchParent)
                        }

                    }.lparams(matchParent, matchParent)
                }
            }.view as T


    fun <T : ViewGroup> getHeader(headerText: String): T =
            activity.UI {
                verticalLayout {
                    id = "main_header_container".toId()

                    linearLayout {
                        backgroundResource = FrameworkViewFactory.getSelectableBackgroundId(activity)

                        imageView(R.drawable.dropdown_arrow) {
                            id = "feature_img_arrow".toId()
                            padding = 10.toDp()
                        }.lparams(30.toDp(), 40.toDp()) {
                            gravity = Gravity.CENTER
                            marginStart = 10.toDp()
                        }

                        textView(FrameworkViewFactory.getSpannedHtml(headerText)) {
                            id = "text_view".toId()
                            setTextAppearance(activity, R.style.DefaultText)
                            textColor = ContextCompat.getColor(activity, R.color.primaryLight)
                            //setTypeface(null, Typeface.BOLD)
                        }.lparams(matchParent) {
                            gravity = Gravity.CENTER
                            marginStart = 10.toDp()
                            verticalMargin = 5.toDp()
                        }

                        setOnClickListener {
                            val parent: ViewGroup = parent as ViewGroup

                            val imgArrow = find<ImageView>("feature_img_arrow".toId())
                            val featureContainer = parent.find<ViewGroup>("header_feature_container".toId())
                            val isExpanded = featureContainer.visibility == View.VISIBLE

                            if (isExpanded) {
                                AnimationUtils.collapse(featureContainer, 2f)
                                AnimationUtils.rotate(imgArrow, false)
                            } else {
                                AnimationUtils.expand(featureContainer, 2f)
                                AnimationUtils.rotate(imgArrow, true)
                            }
                        }
                    }.lparams(matchParent, wrapContent)


                    view {
                        backgroundResource = R.color.primaryWashed
                    }.lparams(width = matchParent, height = 1.toDp())

                    verticalLayout {
                        id = "header_feature_container".toId()
                        visibility = View.GONE
                    }.lparams(matchParent, wrapContent)
                }
            }.view as T

    fun Int.toDp(): Int = Math.ceil((this * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
    fun Int.toPx(): Int = Math.ceil((this / Resources.getSystem().displayMetrics.density).toDouble()).toInt()
    fun String.toId(): Int = ResourceUtils.getIdFromString(this)
}