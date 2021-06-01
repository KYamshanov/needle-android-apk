package ru.undframe.needle.model

import org.json.JSONException
import org.json.JSONObject
import ru.undframe.needle.utils.ResponseStatus

data class User(var id:Long,var username:String,var email:String,var authorization:Boolean,var authStatus: Int,var accessToken:String?,var refreshToken:String?){

    fun fillData(user: User){
        id = user.id
        username = user.username
        email = user.email
        authorization = user.authorization
        authStatus = user.authStatus
        accessToken = user.accessToken
        refreshToken = user.refreshToken
    }

    fun isAuthorization():Boolean{
        return authorization
    }

    companion object {
        fun deserialize(jsonObject: JSONObject): User {
            var user: User = getInstance()
            try {
                val authStatus =
                    if (jsonObject.has("auth_status")) jsonObject.getInt("auth_status") else ResponseStatus.ERROR
                user.authStatus = authStatus
                if (authStatus == ResponseStatus.SUCCESSFUL_AUTHORIZATION) {
                    user.id = jsonObject.getLong("id")
                    user.username = (jsonObject.getString("username"))
                    user.email = (jsonObject.getString("email"))
                    user.accessToken = (jsonObject.getString("access_token"))
                    user.refreshToken = (jsonObject.getString("refresh_token"))
                    user.authStatus = (authStatus)
                    user.authorization = (true)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                user = getInstance()
            }
            return user
        }


        fun getInstance(): User {
            val user: User = User(-1, "null", "null", false, -1, null, null)
            user.authorization = false
            user.authStatus = ResponseStatus.NOT_AUTHORIZED
            return user
        }
    }

}