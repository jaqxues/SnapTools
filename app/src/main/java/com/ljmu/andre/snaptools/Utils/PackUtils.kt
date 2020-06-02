package com.ljmu.andre.snaptools.Utils

import com.ljmu.andre.snaptools.data.PackMetadata


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 02.06.20 - Time 21:00.
 */
object PackUtilsKt {
    fun getFlavourText(flavour: String) =
            when (flavour) {
                "prod" -> "Release"
                "beta" -> "Beta"
                else -> throw IllegalArgumentException("Unknown Pack Flavour $flavour")
            }
}

val PackMetadata.displayName get() =
    (if (flavour == "prod") "" else PackUtilsKt.getFlavourText(flavour)) + "Pack v" + scVersion
