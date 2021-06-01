package ru.undframe.needle.tasks

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import org.json.JSONObject
import ru.undframe.needle.utils.GlobalProperties.serverAddress
import ru.undframe.needle.utils.MultipartUtility
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.function.Consumer

class UploadFileTask(private val accessToken: String, private val file: File) {
    companion object {
        private var emptyFile: File? = null

        init {
            try {
                emptyFile = File.createTempFile("temptile", ".tmp")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun upload(): Int? {
        val requestUrl = "http://${serverAddress}/uploadfile?token=${accessToken}"
        runCatching {
            val multipart = MultipartUtility(requestUrl, Charset.defaultCharset().name())
            multipart.addFilePart("file", file)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return jsonObject.getInt("status")
        }.getOrElse { Log.e("Upload file", "from $requestUrl", it) }
        return null
    }
}