package ru.undframe.needle.utils

import java.io.IOException

interface Properties {
    fun setProperties(key: String, value: String)
    fun getValue(key: String): String?

    @Throws(IOException::class)
    fun save()
}