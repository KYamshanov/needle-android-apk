package ru.undframe.needle.tasks

import android.os.AsyncTask
import org.json.JSONObject
import ru.undframe.needle.model.User
import ru.undframe.needle.utils.GlobalProperties
import ru.undframe.needle.utils.MultipartUtility
import ru.undframe.needle.utils.NCustomer

class AuthUserTask(
    private val username: String,
    private val password: String,
    private val action: NCustomer<User?>
) : AsyncTask<Void?, Void?, User?>() {


    override fun doInBackground(vararg voids: Void?): User? {
        try {
            val requestUrl =
                "http://" + GlobalProperties.ksiteAddress + "/api/auth?login=" + username +
                        "&password=" + password +
                        "&device_id=" + GlobalProperties.deviceData +
                        "&service_id=" + GlobalProperties.serviceName
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return User.deserialize(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return User.getInstance()
    }

    override fun onPostExecute(result: User?) {
        super.onPostExecute(result)
        action.accept(result)
    }
}