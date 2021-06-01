package ru.undframe.needle.tasks

import android.util.Log
import org.json.JSONObject
import ru.undframe.needle.utils.GlobalProperties
import ru.undframe.needle.utils.MultipartUtility
import ru.undframe.needle.utils.ResponseStatus

class CheckAliveTokenTask(
    private val token: String
) {


    suspend fun check(): Int {
        val requestUrl =
            "http://${GlobalProperties.ksiteAddress}/api/alivetoken?access_token=${token}"
        runCatching {
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return jsonObject.getInt("status")
        }.getOrElse {
            Log.e("Authorization", "check alive token $requestUrl", it)
            return ResponseStatus.ERROR
        }
    }
}