package ru.undframe.needle.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.undframe.needle.view.BaseView


class RequestPermission(private val baseView: BaseView, private val permissions: Array<String>) {

    var allowed: (() -> Unit)? = null
    var notAllowed: (() -> Unit)? = {
        AlertDialog.Builder(baseView.context)
            .setTitle("Разрешите права")
            .setMessage(
                "Чтобы получить доступ необходимо потвердить: \n ${
                    permissions.joinToString(
                        ", "
                    )
                }"
            )
            .setNegativeButton("Запретить") { dialog, _ ->
                dialog.dismiss()
                baseView.openNoAccessActivity()
            }
            .setPositiveButton("Разрешить") { dialog, _ ->
                dialog.dismiss()
                baseView.context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }.setOnDismissListener {
                if (closeSourceActivity)
                    baseView.closeActivity()
            }
            .create().show()

    }
    var closeSourceActivity: Boolean = false

    var unverifiedPermission: MutableList<String> = arrayListOf()

    fun checkPermissions(block: RequestPermission.() -> Unit): RequestPermission {
        block.invoke(this)

        permissions.clone().filter {
            !check(it)
        }.apply {

            println("dsfsdf ${this.joinToString(" , ")}}")

            if (this.isEmpty())
                allowed?.invoke()
            else
                requestPermissions(this)
        }



        return this;
    }

    private fun checkAllPermissions(): Boolean {

        var allowedAll = true

        for (permission in permissions) {
            allowedAll = allowedAll && (ContextCompat.checkSelfPermission(
                baseView.context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)
        }

        return allowedAll;
    }

    private fun check(permission: String): Boolean {
        val b = ContextCompat.checkSelfPermission(
            baseView.context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        println("$permission is $b")
        return b
    }

    private fun requestPermissions(permissions: List<String>) {

        println("request permissions ${permissions.joinToString(", ")}")

        if (baseView.context is Activity) {
            val actualRequestPermissionId = getActualRequestPermissionId()
            ActivityCompat.requestPermissions(
                baseView.context as Activity, permissions.toTypedArray(),
                actualRequestPermissionId
            )
            unverifiedPermission.addAll(permissions)
            requestPermissions[actualRequestPermissionId] = this

        }
    }

    companion object {

        private var requestPermissionId: Int = 0;
        private val requestPermissions: MutableMap<Int, RequestPermission> = HashMap()

        fun getActualRequestPermissionId(): Int {
            return requestPermissionId++;
        }

        @JvmStatic
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {

            println("TEST1111 ${permissions.joinToString(" , ")}")

            if (requestPermissions.containsKey(requestCode)) {
                val requestPermission = requestPermissions[requestCode]!!
                requestPermission.unverifiedPermission.removeAll { s ->
                    permissions.contains(s)
                }
                if (requestPermission.unverifiedPermission.size == 0) {
                    requestPermissions.remove(requestCode)

                    println("REMOVE YES")

                    if (requestPermission.checkAllPermissions())
                        requestPermission.allowed?.invoke()
                    else
                        requestPermission.notAllowed?.invoke()

                }
            }
        }

    }

}