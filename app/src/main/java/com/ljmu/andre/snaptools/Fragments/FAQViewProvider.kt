package com.ljmu.andre.snaptools.Fragments

import android.app.Activity
import android.content.res.Resources
import androidx.core.content.ContextCompat
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.Utils.AnimationUtils
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString
import org.jetbrains.anko.*

@Suppress("DEPRECATION", "UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class FAQViewProvider(val activity: Activity) {
    var expandedHeader: View? = null
    var expandedHeaderArrow: View? = null
    var expandedSubHeader: View? = null
    var expandedSubHeaderArrow: View? = null

    fun <T : ViewGroup> getHeader(headerText: String): T =
            activity.UI {
                verticalLayout {
                    id = "main_header_container".toId()

                    verticalLayout {
                        id = "header_container".toId()

                        linearLayout {
                            backgroundResource = getSelectableBackgroundId(activity)

                            imageView(R.drawable.dropdown_arrow) {
                                id = "faq_img_arrow".toId()
                                padding = 10.toDp()
                            }.lparams(30.toDp(), 40.toDp()) {
                                gravity = Gravity.CENTER
                                marginStart = 10.toDp()
                            }

                            textView(getSpannedHtml(headerText)) {
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
                                val mainContainer: ViewGroup = parent.parent as ViewGroup

                                val imgArrow = mainContainer.find<ImageView>("faq_img_arrow".toId())
                                val faqContainer = mainContainer.find<ViewGroup>("header_faq_container".toId())
                                val isExpanded = faqContainer.visibility == View.VISIBLE

                                if (isExpanded) {
                                    collapseLastSubHeader()

                                    expandedHeader = null
                                    expandedHeaderArrow = null
                                    expandedSubHeader = null
                                    expandedSubHeaderArrow = null

                                    AnimationUtils.collapse(faqContainer, 2f)
                                    AnimationUtils.rotate(imgArrow, false)
                                } else {
                                    collapseLastHeader()

                                    expandedHeader = faqContainer
                                    expandedHeaderArrow = imgArrow

                                    AnimationUtils.expand(faqContainer, 2f)
                                    AnimationUtils.rotate(imgArrow, true)
                                }
                            }
                        }.lparams(matchParent, wrapContent)

                        view {
                            backgroundResource = R.color.primaryWashed
                        }.lparams(width = matchParent, height = 1.toDp())
                    }

                    verticalLayout {
                        id = "header_faq_container".toId()
                        visibility = View.GONE
                    }.lparams(matchParent, wrapContent)

                }
            }.view as T

    fun <T : ViewGroup> getSubHeader(headerText: String): T =
            activity.UI {
                verticalLayout {
                    id = "main_sub_header_container".toId()

                    verticalLayout {
                        id = "sub_header_container".toId()

                        linearLayout {
                            backgroundResource = getSelectableBackgroundId(activity)

                            imageView(R.drawable.dropdown_arrow) {
                                id = "faq_img_arrow".toId()
                                padding = 10.toDp()
                                setColorFilter(ContextCompat.getColor(activity, R.color.textPrimary))
                            }.lparams(30.toDp(), 40.toDp()) {
                                gravity = Gravity.CENTER
                                marginStart = 10.toDp()
                            }

                            textView(getSpannedHtml(headerText)) {
                                id = "text_view".toId()
                                setTextAppearance(activity, R.style.DefaultText)
                                //setTypeface(null, Typeface.BOLD)
                            }.lparams(matchParent) {
                                gravity = Gravity.CENTER
                                marginStart = 10.toDp()
                                verticalMargin = 5.toDp()
                            }

                            setOnClickListener {
                                val parent: ViewGroup = parent as ViewGroup
                                val mainContainer: ViewGroup = parent.parent as ViewGroup

                                val imgArrow = mainContainer.find<ImageView>("faq_img_arrow".toId())
                                val faqContainer = mainContainer.find<ViewGroup>("header_faq_container".toId())
                                val isExpanded = faqContainer.visibility == View.VISIBLE

                                if (isExpanded) {
                                    expandedSubHeader = null
                                    expandedSubHeaderArrow = null
                                    AnimationUtils.collapse(faqContainer, 2f)
                                    AnimationUtils.rotate(imgArrow, false)
                                } else {
                                    collapseLastSubHeader()

                                    expandedSubHeader = faqContainer
                                    expandedSubHeaderArrow = imgArrow

                                    AnimationUtils.expand(faqContainer, 2f)
                                    AnimationUtils.rotate(imgArrow, true)
                                }
                            }
                        }.lparams(matchParent, wrapContent)

                        view {
                            backgroundResource = R.color.textPrimaryWashed
                        }.lparams(width = matchParent, height = 1.toDp())
                    }

                    verticalLayout {
                        id = "header_faq_container".toId()
                        visibility = View.GONE
                    }.lparams(matchParent, wrapContent)
                }
            }.view as T


    fun <T : ViewGroup> getAnswerLayout(answerText: String): T =
            activity.UI {
                verticalLayout {
                    id = "answer_container".toId()

                    textView(getSpannedHtml(answerText)) {
                        id = "text_view".toId()
                        verticalPadding = 10.toDp()
                        setTextAppearance(activity, R.style.DefaultText)
                        textColor = ContextCompat.getColor(activity, R.color.textTertiary)
                        movementMethod = LinkMovementMethod.getInstance()

                        if (answerText.startsWith("<center>"))
                            gravity = Gravity.CENTER
                    }.lparams(matchParent, wrapContent) {
                        horizontalMargin = 10.toDp()
                    }
                }
            }.view as T

    fun collapseLastHeader() {
        if (expandedHeader != null) {
            AnimationUtils.collapse(expandedHeader, 2f)

            val isToolbarArrow = expandedHeaderArrow?.id ?: -1 == R.id.toolbar_img_arrow
            AnimationUtils.rotate(expandedHeaderArrow, isToolbarArrow)
        }

        collapseLastSubHeader()
    }

    fun collapseLastSubHeader() {
        if (expandedSubHeader != null) {
            AnimationUtils.collapse(expandedSubHeader, 2f)
            AnimationUtils.rotate(expandedSubHeaderArrow, false)
        }
    }

    fun Int.toDp(): Int = Math.ceil((this * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
    fun Int.toPx(): Int = Math.ceil((this / Resources.getSystem().displayMetrics.density).toDouble()).toInt()
    fun String.toId(): Int = getIdFromString(this)
}