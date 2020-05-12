package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.GsonPreferences.Preferences.putPref
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.Provider
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.*
import com.ljmu.andre.snaptools.Utils.Constants
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import org.jetbrains.anko.*

@Suppress("UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class NowPlayingView {
    private val callableViewList: List<Provider<Activity, ViewGroup>> = arrayListOf(
            Provider {
                getPlayer0(it, true)
            },
            Provider {
                getPlayer0(it, false)
            },
            Provider {
                getPlayer1(it, true)
            },
            Provider {
                getPlayer1(it, false)
            },
            Provider {
                getPlayer2(it, true)
            },
            Provider {
                getPlayer2(it, false)
            },
            Provider {
                getPlayer3(it, true)
            },
            Provider {
                getPlayer3(it, false)
            },
            Provider {
                getPlayer4(it, true)
            },
            Provider {
                getPlayer4(it, false)
            }
    )

    fun <T : ViewGroup> getCurrentPlayerView(activity: Activity): T =
            getPlayerView(activity, false)

    fun <T : ViewGroup> getPlayerView(activity: Activity, shouldIncrement: Boolean): T {
        var currentPlayerIndex: Int = getPref(CURRENT_NOW_PLAYING_VIEW)

        if (shouldIncrement)
            currentPlayerIndex++

        if (currentPlayerIndex >= getPlayerCount())
            currentPlayerIndex = 0

        if (shouldIncrement)
            putPref(CURRENT_NOW_PLAYING_VIEW, currentPlayerIndex)

        return callableViewList[currentPlayerIndex].call(activity) as T
    }

    private fun getPlayerCount(): Int = callableViewList.size

    private fun <T : ViewGroup> getPlayer0(activity: Activity, isDark: Boolean): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val bgColor: Int = if (isDark) Color.parseColor("#E0333333") else Color.parseColor("#E0DDDDDD")
                val txtColor: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textPrimary"))
                        else ContextCompat.getColor(modContext, getColor(modContext, "textSecondary"))
                val txtColor2: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textTertiary"))
                        else Color.parseColor("#414141")

                val imageSize: Int = getPref(NOW_PLAYING_IMAGE_SIZE)

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)

                    verticalLayout {
                        id = getIdFromString("now_playing_container")
                        background = getBorderedDrawable(
                                bgColor,
                                null,//Color.BLACK,
                                0,
                                dip(10)
                        )
                        padding = dip(7)

                        frameLayout {
                            padding = dip(2)
                            //background = getBorderedDrawable(null, Color.BLACK, dip(2))

                            imageView {
                                id = getIdFromString("now_playing_art")

                                if (!getPref<Boolean>(FILTER_NOW_PLAYING_HIDE_EMPTY_ART)) {
                                    backgroundResource = if (Constants.getApkVersionCode() >= 65) {
                                        getDrawable(modContext, "music_record_primary_dark")
                                    } else {
                                        getDrawable(modContext, "delete")
                                    }
                                }
                            }.lparams(width = imageSize, height = imageSize)
                        }.lparams(width = wrapContent, height = wrapContent) {
                            gravity = Gravity.CENTER
                            bottomMargin = dip(5)
                        }

                        textView("placeholder") {
                            id = getIdFromString("now_playing_title")
                            gravity = Gravity.CENTER
                            textSize = 15f
                            textColor = txtColor
                        }.lparams(width = matchParent, height = wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        textView("placeholder") {
                            id = getIdFromString("now_playing_artist")
                            gravity = Gravity.CENTER
                            textSize = 13f
                            textColor = txtColor2
                        }.lparams(width = matchParent, height = wrapContent) {
                            gravity = Gravity.CENTER
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentBottom()
                        centerHorizontally()
                        bottomMargin = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)
                    }
                }
            }.view as T

    private fun <T : ViewGroup> getPlayer1(activity: Activity, isDark: Boolean): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val bgColor: Int =
                        if (isDark) Color.parseColor("#E0333333")
                        else Color.parseColor("#E0DDDDDD")
                val txtColor: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textPrimary"))
                        else ContextCompat.getColor(modContext, getColor(modContext, "textSecondary"))
                val txtColor2: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textTertiary"))
                        else Color.parseColor("#414141")
                val imageSize: Int = getPref(NOW_PLAYING_IMAGE_SIZE)

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)

                    linearLayout {
                        id = getIdFromString("now_playing_container")
                        background = getBorderedDrawable(
                                bgColor,
                                null,//Color.BLACK,
                                0,
                                dip(10)
                        )
                        padding = dip(7)

                        frameLayout {
                            padding = dip(2)
                            //background = getBorderedDrawable(null, Color.BLACK, dip(2))

                            imageView {
                                id = getIdFromString("now_playing_art")

                                if (!getPref<Boolean>(FILTER_NOW_PLAYING_HIDE_EMPTY_ART)) {
                                    backgroundResource = if (Constants.getApkVersionCode() >= 65) {
                                        getDrawable(modContext, "music_record_primary_dark")
                                    } else {
                                        getDrawable(modContext, "delete")
                                    }
                                }
                            }.lparams(width = imageSize, height = imageSize)
                        }.lparams(width = wrapContent, height = wrapContent) {
                            rightMargin = dip(10)
                        }

                        verticalLayout {
                            rightPadding = dip(15)

                            textView("placeholder") {
                                id = getIdFromString("now_playing_title")
                                gravity = Gravity.CENTER
                                textSize = 15f
                                textColor = txtColor
                            }.lparams(width = wrapContent, height = wrapContent) {
                                weight = 1f
                                gravity = Gravity.CENTER
                            }

                            textView("placeholder") {
                                id = getIdFromString("now_playing_artist")
                                gravity = Gravity.CENTER
                                textSize = 15f
                                textColor = txtColor2
                            }.lparams(width = wrapContent, height = wrapContent) {
                                weight = 1f
                                gravity = Gravity.CENTER
                            }
                        }.lparams(width = wrapContent, height = matchParent) {
                            weight = 1f
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentBottom()
                        centerHorizontally()
                        margin = dip(10)
                        bottomMargin = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)
                    }
                }
            }.view as T

    private fun <T : ViewGroup> getPlayer2(activity: Activity, isDark: Boolean): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val bgColor: Int = if (isDark) Color.parseColor("#E0333333") else Color.parseColor("#E0DDDDDD")
                val bannerColor: Int = if (isDark) Color.parseColor("#AA333333") else Color.parseColor("#AADDDDDD")
                val txtColor: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textPrimary"))
                        else ContextCompat.getColor(modContext, getColor(modContext, "textSecondary"))
                val txtColor2: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textTertiary"))
                        else Color.parseColor("#414141")
                val imageSize: Int = getPref(NOW_PLAYING_IMAGE_SIZE)

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)

                    linearLayout {
                        id = getIdFromString("now_playing_container")
                        background = getBorderedDrawable(
                                bgColor,
                                null,//Color.BLACK,
                                0
                        )

                        relativeLayout {
                            //background = getBorderedDrawable(null, Color.BLACK, dip(2))
                            id = getIdFromString("now_playing_art")

                            if (!getPref<Boolean>(FILTER_NOW_PLAYING_HIDE_EMPTY_ART)) {
                                backgroundResource = if (Constants.getApkVersionCode() >= 65) {
                                    getDrawable(modContext, "music_record_primary_dark")
                                } else {
                                    getDrawable(modContext, "delete")
                                }
                            }

                            verticalLayout {
                                backgroundColor = bannerColor

                                textView("placeholder") {
                                    id = getIdFromString("now_playing_title")
                                    gravity = Gravity.CENTER
                                    textSize = 15f
                                    textColor = txtColor
                                }.lparams(width = matchParent, height = matchParent) {
                                    weight = 1f
                                }

                                textView("placeholder") {
                                    id = getIdFromString("now_playing_artist")
                                    gravity = Gravity.CENTER
                                    textSize = 15f
                                    textColor = txtColor2
                                }.lparams(width = matchParent, height = matchParent) {
                                    weight = 1f
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                alignParentBottom()
                            }
                        }.lparams(width = imageSize, height = imageSize)
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentBottom()
                        centerHorizontally()
                        bottomMargin = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)
                    }
                }
            }.view as T


    private fun <T : ViewGroup> getPlayer3(activity: Activity, isDark: Boolean): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val bgColor: Int = if (isDark) Color.parseColor("#E0333333") else Color.parseColor("#E0DDDDDD")
                val txtColor: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textPrimary"))
                        else ContextCompat.getColor(modContext, getColor(modContext, "textSecondary"))
                val txtColor2: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textTertiary"))
                        else Color.parseColor("#414141")

                val imageSize: Int = getPref(NOW_PLAYING_IMAGE_SIZE)

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)

                    verticalLayout {
                        id = getIdFromString("now_playing_container")

                        frameLayout {
                            backgroundColor = bgColor

                            imageView {
                                id = getIdFromString("now_playing_art")

                                if (!getPref<Boolean>(FILTER_NOW_PLAYING_HIDE_EMPTY_ART)) {
                                    backgroundResource = if (Constants.getApkVersionCode() >= 65) {
                                        getDrawable(modContext, "music_record_primary_dark")
                                    } else {
                                        getDrawable(modContext, "delete")
                                    }
                                }
                            }.lparams(width = imageSize, height = imageSize)
                        }.lparams(width = wrapContent, height = wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        verticalLayout {
                            background = getBorderedDrawable(
                                    bgColor,
                                    null,//Color.BLACK,
                                    0,
                                    dip(10)
                            )
                            padding = dip(15)

                            textView("placeholder") {
                                id = getIdFromString("now_playing_title")
                                gravity = Gravity.CENTER
                                textSize = 15f
                                textColor = txtColor
                            }.lparams(width = matchParent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }

                            textView("placeholder") {
                                id = getIdFromString("now_playing_artist")
                                gravity = Gravity.CENTER
                                textSize = 13f
                                textColor = txtColor2
                            }.lparams(width = matchParent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(width = imageSize + dip(20), height = wrapContent)
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentBottom()
                        centerHorizontally()
                        bottomMargin = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)
                    }
                }
            }.view as T

    private fun <T : ViewGroup> getPlayer4(activity: Activity, isDark: Boolean): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val bgColor: Int = if (isDark) Color.parseColor("#E0333333") else Color.parseColor("#E0DDDDDD")
                val txtColor: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textPrimary"))
                        else ContextCompat.getColor(modContext, getColor(modContext, "textSecondary"))
                val txtColor2: Int =
                        if (isDark) ContextCompat.getColor(modContext, getColor(modContext, "textTertiary"))
                        else Color.parseColor("#414141")

                val imageSize: Int = getPref(NOW_PLAYING_IMAGE_SIZE)

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)

                    verticalLayout {
                        id = getIdFromString("now_playing_container")

                        imageView {
                            id = getIdFromString("now_playing_art")
                            visibility = View.GONE
                        }.lparams(width = imageSize, height = imageSize) {
                            gravity = Gravity.CENTER
                        }

                        verticalLayout {
                            background = getBorderedDrawable(
                                    bgColor,
                                    null,//Color.BLACK,
                                    0,
                                    dip(10)
                            )
                            padding = dip(15)

                            textView("placeholder") {
                                id = getIdFromString("now_playing_title")
                                gravity = Gravity.CENTER
                                textSize = 15f
                                textColor = txtColor
                            }.lparams(width = matchParent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }

                            textView("placeholder") {
                                id = getIdFromString("now_playing_artist")
                                gravity = Gravity.CENTER
                                textSize = 13f
                                textColor = txtColor2
                            }.lparams(width = matchParent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(width = wrapContent, height = wrapContent)
                    }.lparams(width = wrapContent, height = wrapContent) {
                        alignParentBottom()
                        centerHorizontally()
                        bottomMargin = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)
                    }
                }
            }.view as T

    fun <T : ViewGroup> getPlayerPositionController(activity: Activity, onSeekBarProgress: OnSeekBarProgress): T =
            getModuleContext(activity).UI {
                val modContext: Context = getModuleContext(activity)
                val lineColorId: Int = getColor(modContext, "primary")

                relativeLayout {
                    lparams(width = matchParent, height = matchParent)
                    backgroundColor = Color.parseColor("#AA000000")

                    view {
                        backgroundResource = lineColorId
                        id = getIdFromString("bottom_margin_line")
                        visibility = View.INVISIBLE
                    }.lparams(width = dip(2), height = getPref<Int>(NOW_PLAYING_BOTTOM_MARGIN)) {
                        centerHorizontally()
                        alignParentBottom()
                    }

                    frameLayout {
                        id = getIdFromString("player_container")
                    }.lparams(width = wrapContent, height = wrapContent) {
                        centerHorizontally()
                        above(getIdFromString("bottom_margin_line"))
                    }

                    linearLayout {
                        imageButton(getDrawable(modContext, "cancel_96")) {
                            id = getIdFromString("button_close_player_settings")
                            backgroundResource = 0
                            setColorFilter(Color.WHITE)
                        }.lparams(width = dip(40), height = dip(40)) {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }

                        relativeLayout {
                            val artSizeText = TextView(modContext)
                            artSizeText.setTextColor(Color.WHITE)
                            artSizeText.lparams(width = wrapContent, height = wrapContent) {
                                below(getIdFromString("art_size_scrollbar"))
                                centerHorizontally()
                            }
                            artSizeText.text = getPref<Int>(NOW_PLAYING_IMAGE_SIZE).toString() + "px"

                            val artSizeSeekBar = getBasicSeekbar(
                                    modContext,
                                    600,
                                    0,
                                    getPref(NOW_PLAYING_IMAGE_SIZE),
                                    OnSeekBarResult { _, progress ->
                                        putPref(NOW_PLAYING_IMAGE_SIZE, progress)
                                    },
                                    onSeekBarProgress,
                                    Pair<String, TextView>("%spx", artSizeText)
                            ).lparams(width = matchParent, height = dip(40)) {
                            }
                            artSizeSeekBar.id = getIdFromString("art_size_scrollbar")
                            artSizeSeekBar.progressDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                            artSizeSeekBar.thumb.setColorFilter(ContextCompat.getColor(modContext, getColor(modContext, "primary")), PorterDuff.Mode.SRC_ATOP)

                            addView(artSizeSeekBar)
                            addView(artSizeText)
                        }.lparams(width = matchParent, height = wrapContent) {
                            rightMargin = dip(40)
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(10)
                    }
                }
            }.view as T
}