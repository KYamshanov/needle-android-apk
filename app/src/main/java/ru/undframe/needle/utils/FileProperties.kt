package ru.undframe.needle.utils

import android.util.Base64
import java.io.*
import java.util.*

class FileProperties(file: File) : Properties {
    private val properties: MutableMap<String, String?>
    private val file: File
    override fun setProperties(key: String, value: String?) {
        if (value != null) properties[key] = value else properties.remove(key)
    }

    override fun getValue(key: String): String? {
        return if (properties.containsKey(key)) properties[key] else null
    }

    @Throws(IOException::class)
    override suspend fun save() {
        val fwOb = FileWriter(file, false)
        val pwOb = PrintWriter(fwOb, false)
        for ((key, value) in properties) {
            pwOb.println(String(Base64.encode("$key=$value".toByteArray(), Base64.NO_PADDING)).replace("\n",""))
        }
        pwOb.flush()
        pwOb.close()
        fwOb.close()
    }

    override fun toString(): String {
        return "FileProperties{" +
                "properties=" + properties +
                '}'
    }

    fun remove(key: String) {
        properties.remove(key)
    }

    init {
        val propertyMap: MutableMap<String, String?> = HashMap()
        this.file = file
        properties = propertyMap
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }

        val lines = ArrayList<String>()

        val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
        try {
            var readLine: String? = reader.readLine()
            while (readLine != null) {
                lines.add(readLine)
                readLine = reader.readLine()
            }
            for (line in lines) {
                val decodeString = String(Base64.decode(line.toByteArray(), Base64.NO_PADDING))
                val property = decodeString.split("=", limit = 2).toTypedArray()
                if (property.size == 2) propertyMap[property[0]] = property[1]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader.close()
        }
    }
}