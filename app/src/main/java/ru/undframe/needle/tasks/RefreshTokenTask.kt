package ru.undframe.needle.tasks

import android.os.AsyncTask
import org.json.JSONObject
import ru.undframe.needle.model.User
import ru.undframe.needle.model.User.Companion.deserialize
import ru.undframe.needle.model.User.Companion.getInstance
import ru.undframe.needle.utils.GlobalProperties.deviceData
import ru.undframe.needle.utils.GlobalProperties.ksiteAddress
import ru.undframe.needle.utils.GlobalProperties.serviceName
import ru.undframe.needle.utils.MultipartUtility
import ru.undframe.needle.utils.NConsumer

class RefreshTokenTask(
    private val refreshToken: String,
    private val userId: Long,
    private val action: NConsumer<User?>
) : AsyncTask<Void?, Void?, User?>() {

    override fun doInBackground(vararg params: Void?): User? {
        try {
            val requestUrl = "http://" + ksiteAddress + "/api/refresh?id=" + userId +
                    "&device_id=" + deviceData +
                    "&refresh_token=" + refreshToken +
                    "&service_id=" + serviceName
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return deserialize(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getInstance()
    }

    override fun onPostExecute(result: User?) {
        super.onPostExecute(result)
        action.accept(result)
    }
}