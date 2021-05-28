package ru.undframe.needle.tasks

import android.os.AsyncTask
import org.json.JSONObject
import ru.undframe.needle.utils.GlobalProperties
import ru.undframe.needle.utils.MultipartUtility
import ru.undframe.needle.utils.NConsumer
import ru.undframe.needle.utils.ResponseStatus

class CheckAliveTokenTask(
    private val token: String,
    private val action: NConsumer<Int>
) : AsyncTask<Void?, Void?, Int>() {


    override fun doInBackground(vararg voids: Void?): Int {
        try {
            val requestUrl =
                "http://${GlobalProperties.ksiteAddress}/api/is_alive_token?token=${token}"
            val charset = "UTF-8"
            val multipart = MultipartUtility(requestUrl, charset)
            val response = multipart.finish()
            val stringJson = StringBuilder()
            for (s in response) {
                stringJson.append(s)
            }
            val jsonObject = JSONObject(stringJson.toString())
            return jsonObject.getInt("status")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResponseStatus.ERROR
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        action.accept(result)
    }
}