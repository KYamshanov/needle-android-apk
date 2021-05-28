package ru.undframe.needle.tasks

import android.os.AsyncTask
import android.os.Build
import org.json.JSONObject
import ru.undframe.needle.utils.GlobalProperties.serverAddress
import ru.undframe.needle.utils.MultipartUtility
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.function.Consumer

class UploadFileTask(private val accessToken:String,private val file: File, private val action: Consumer<Int>) :
    AsyncTask<Void?, Void?, Int>() {
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

    override fun doInBackground(vararg params: Void?): Int {

        try {
            val requestUrl = "http://${serverAddress}/uploadfile?token=${accessToken}"

            println(requestUrl)

            val multipart = MultipartUtility(requestUrl, Charset.defaultCharset().name())
            multipart.addFilePart("file", file)
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
        return 1
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            action.accept(result)
        }
    }
}