package ru.undframe.needle.utils

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.undframe.needle.encryption.SimpleCipher
import ru.undframe.needle.model.User
import ru.undframe.needle.model.User.Companion.getInstance
import ru.undframe.needle.tasks.AuthUserTask
import ru.undframe.needle.tasks.RefreshTokenTask
import ru.undframe.needle.utils.GlobalProperties.getFileProperties
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

class UserFactory private constructor() {
    private var currentUser: User = User.getInstance()


    fun authCurrentUser(
        userLogin: String,
        password: String,
        savePassword: Boolean,
        action: NConsumer<User>
    ) {


        try {
            val aesKey = SecretKeySpec(
                Base64.decode(
                    SimpleCipher.PASSWORD_CIPHER_KEY.toByteArray(),
                    Base64.DEFAULT
                ), "AES"
            )
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, aesKey)
            val encrypted = cipher.doFinal(password.toByteArray())
            val s = DatatypeConverter.printBase64Binary(encrypted)

            CoroutineScope(Dispatchers.IO).launch {
                val authUser = AuthUserTask(userLogin, s).auth()
                setCurrentUser(authUser)
                withContext(Dispatchers.Main) { action.accept(currentUser) }
                if (authUser.isAuthorization()) {
                    val properties = GlobalProperties.getFileProperties()
                    if (properties != null)
                        saveInFile(properties, authUser, savePassword)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
    }

    fun refreshCurrentUser(action: NConsumer<User>) {
        if (currentUser.refreshToken != null) {

            CoroutineScope(Dispatchers.IO).launch {
                val u = RefreshTokenTask(currentUser.refreshToken!!, currentUser.id).refresh()
                setCurrentUser(u)
                action.accept(u)
                if (u.isAuthorization()) {
                    saveUserData(u)
                }
            }
        }
    }

    private fun saveUserData(u: User) {
        runCatching {
            val properties = getFileProperties()
            if (properties != null) {
                val save =
                    java.lang.Boolean.parseBoolean(
                        properties.getValue("save") ?: "false"
                    )
                if (save) {
                    saveInFile(properties, u, save)
                }
            }
        }.getOrElse {
            Log.e("Authorization", "refresh current user. load data", it)
        }
    }

    private fun saveInFile(
        properties: FileProperties,
        u: User,
        save: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                properties.setProperties(
                    "refresh_token",
                    if (save) String(
                        Base64.encode(
                            SimpleCipher.encodePassword(u.refreshToken!!.toByteArray()),
                            Base64.DEFAULT
                        )
                    ).replace("\n", "")
                    else null
                )
                properties.setProperties("user_id", if (save) u.id.toString() else null)
                properties.setProperties(
                    "save",
                    java.lang.Boolean.toString(save)
                )
                properties.save()
            }.getOrElse {
                Log.e("Authorization", "refresh current user. save data", it)
            }
        }
    }

    fun refreshSavedUser(action: NConsumer<User?>) {
        val properties = GlobalProperties.getFileProperties()

        if (properties != null) {
            val refreshTokenOptional = properties.getValue("refresh_token")
            val userIdOptional = properties.getValue("user_id")
            if (refreshTokenOptional != null && userIdOptional != null) {
                runCatching {
                    var refreshToken = refreshTokenOptional
                    val userId = userIdOptional.toLong()
                    refreshToken = String(
                        SimpleCipher.decodePassword(
                            Base64.decode(
                                refreshToken.toByteArray(),
                                Base64.DEFAULT
                            )
                        )
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val u = RefreshTokenTask(refreshToken, userId).refresh()
                        setCurrentUser(u)
                        action.accept(u)
                        if (u.isAuthorization()) {
                            saveInFile(
                                properties,
                                u,
                                (properties.getValue("save") ?: "false").toBoolean()
                            )
                        }
                    }
                }.getOrElse {
                    Log.e("Authorization", "refresh saved user", it)
                    //TODO делать что-то если не загружены
                }
            }
        }
    }

    fun getCurrentUser(): User {
        return currentUser
    }

    fun exit() {
        setCurrentUser(User.getInstance())
        val properties = GlobalProperties.getFileProperties()
        if (properties != null)
            CoroutineScope(Dispatchers.IO).launch {
                properties.remove("refresh_token")
                properties.remove("user_id")
                properties.remove("save")
                runCatching { properties.save() }
                    .getOrElse { Log.e("Properties", "save properties", it) }
            }
    }

    private fun setCurrentUser(user: User) {
        this.currentUser.fillData(user)
    }


    companion object {

        private var userFactory: UserFactory? = null

        @JvmStatic
        fun getInstance(): UserFactory {
            if (userFactory == null) userFactory = UserFactory()
            return userFactory!!
        }


    }

}