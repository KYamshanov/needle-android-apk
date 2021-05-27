package ru.undframe.needle.utils

import android.util.Base64
import org.json.JSONException
import org.json.JSONObject

object GlobalProperties {

        var serverAddress = "10.0.2.2:8080"
        var ksiteAddress = "10.0.2.2:8080"
        var serviceName = "NEEDLE"
        var currentDeviceId = "overDiff_device"
        var ip = "ip is null"

        private var fileProperties: FileProperties? = null

        fun getFileProperties(): FileProperties? {
            return fileProperties
        }

        fun setFileProperties(properties: FileProperties) {
            GlobalProperties.fileProperties = properties
        }

        val deviceData: String
            get() {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ip", ip)
                    jsonObject.put("id", currentDeviceId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return String(Base64.encode(jsonObject.toString().toByteArray(), Base64.DEFAULT))
            }
}