package com.ljmu.andre.snaptools.data

import com.jaqxues.akrolyb.pack.PackMetadata as IPackMetadata


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 27.05.20 - Time 08:54.
 */
interface PackMetadata : IPackMetadata {
    val isTutorial: Boolean
    val latest: Boolean
    val flavour: String
    val scVersion: String
    val name: String
}

data class ServerPackMetadata(
        override val devPack: Boolean,
        override val packVersion: String,
        override val packVersionCode: Int,
        override val packImplClass: String,
        override val minApkVersionCode: Int,
        override val isTutorial: Boolean,
        override val latest: Boolean,
        override val flavour: String,
        override val scVersion: String,
        override val name: String,

        val description: String,
        val isInstalled: Boolean,
        val hasUpdate: Boolean
) : PackMetadata

data class LocalPackMetadata(
        override val isTutorial: Boolean,
        override val latest: Boolean,
        override val flavour: String,
        override val scVersion: String,
        override val name: String,
        override val devPack: Boolean,
        override val packVersion: String,
        override val packVersionCode: Int,
        override val packImplClass: String,
        override val minApkVersionCode: Int
) : PackMetadata