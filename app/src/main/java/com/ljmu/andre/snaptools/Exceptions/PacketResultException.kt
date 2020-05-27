package com.ljmu.andre.snaptools.Exceptions


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 26.05.20 - Time 14:09.
 */
data class PacketResultException(val title: String, override val message: String, val errorCode: Int): Exception(message)
