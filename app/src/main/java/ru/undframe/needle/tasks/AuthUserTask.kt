package ru.undframe.needle.tasks

import android.util.Log
import org.json.JSONObject
import ru.undframe.needle.model.User
import ru.undframe.needle.utils.GlobalProperties
import ru.undframe.needle.utils.MultipartUtility

class AuthUserTask(
    private val username: String,
    private val password: String
) {


    suspend fun auth(): User {

        runCatching {
            val requestUrl =
                "http://${GlobalProperties.ksiteAddress}/api/auth?login=$username" +
                        "&password=$password" +
                        "&device_id=${GlobalProperties.deviceData}" +
                        "&service_id=${GlobalProperties.serviceName}"
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return User.deserialize(jsonObject)
        }.getOrElse {
            Log.e("Authorization", "auth user task error", it)
            return User.getInstance()
        }
    }

}