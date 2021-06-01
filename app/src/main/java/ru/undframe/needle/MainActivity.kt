package ru.undframe.needle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.undframe.needle.presenters.MainPresenter
import ru.undframe.needle.utils.*
import ru.undframe.needle.view.AuthorizationView
import ru.undframe.needle.view.SavePhotoActivity
import ru.undframe.needle.view.MainView
import ru.undframe.needle.view.NoAccessActivity
import java.io.File
import java.lang.NullPointerException


class MainActivity : AppCompatActivity(), MainView {

    private lateinit var cameraButton: ImageButton;

    private lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this);

        RequestPermission(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        ).checkPermissions {
            allowed = {
                makeCall()
            }
            closeSourceActivity = true
        }

        cameraButton = findViewById(R.id.camera_button)

        val recyclerView = findViewById<RecyclerView>(R.id.preview_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MainAdapter()

        cameraButton.setOnClickListener {
            clickOnCameraButton()
        }

        val deviceId = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)

        GlobalProperties.currentDeviceId = deviceId
        GlobalProperties.ip = ip
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        RequestPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun makeCall() {
        val rawProperties =
            RawProperties(resources.openRawResource(R.raw.application))

        if (rawProperties.getValue("server") != null)
            GlobalProperties.serverAddress = rawProperties.getValue("server")!!
        if (rawProperties.getValue("ksite") != null)
            GlobalProperties.ksiteAddress = rawProperties.getValue("ksite")!!


        GlobalProperties.setFileProperties(FileProperties(File(filesDir, "config.data")))


        if (!UserFactory.getInstance().getCurrentUser().authorization)
            openAuthorizationView()

        Log.d("START", "Ksite ${GlobalProperties.ksiteAddress}")
        Log.d("START", "server ${GlobalProperties.serverAddress}")


    }

    class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.preview_main_item, parent, false)
            return ViewHolder(inflate)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return 5;
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun openAuthorizationView() {
        startActivity(Intent(this, AuthorizationView::class.java))
    }

    override fun getContext(): Context {
        return this
    }

    override fun clickOnCameraButton() {
        startActivity(Intent(this, SavePhotoActivity::class.java))
    }

    override fun openNoAccessActivity() {
        startActivity(Intent(this, NoAccessActivity::class.java))
    }

    override fun closeActivity() {
        finish()
    }


}

