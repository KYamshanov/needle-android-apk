package ru.undframe.needle.tasks

import android.util.Log
import org.json.JSONObject
import ru.undframe.needle.model.User
import ru.undframe.needle.model.User.Companion.deserialize
import ru.undframe.needle.model.User.Companion.getInstance
import ru.undframe.needle.utils.GlobalProperties.deviceData
import ru.undframe.needle.utils.GlobalProperties.ksiteAddress
import ru.undframe.needle.utils.GlobalProperties.serviceName
import ru.undframe.needle.utils.MultipartUtility

class RefreshTokenTask(
    private val refreshToken: String,
    private val userId: Long
) {

    suspend fun refresh(): User {

        runCatching {
            val requestUrl = "http://$ksiteAddress/api/refresh?id=" + userId +
                    "&device_id=$deviceData" +
                    "&refresh_token=$refreshToken" +
                    "&service_id=$serviceName"
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return deserialize(jsonObject)
        }.getOrElse {
            Log.e("Authorization", "refresh token", it)
            return getInstance()
        }
    }
}