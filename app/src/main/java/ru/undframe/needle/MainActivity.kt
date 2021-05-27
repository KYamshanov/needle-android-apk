package ru.undframe.needle

import android.Manifest
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.undframe.needle.presenters.MainPresenter
import ru.undframe.needle.utils.FileProperties
import ru.undframe.needle.utils.GlobalProperties
import ru.undframe.needle.utils.RawProperties
import ru.undframe.needle.utils.UserFactory
import ru.undframe.needle.view.AuthorizationView
import ru.undframe.needle.view.CreatePhotoActivity
import ru.undframe.needle.view.MainView
import java.io.File


class MainActivity : AppCompatActivity(), MainView {

    private val PERMISSION_CALL = 127


    private lateinit var cameraButton:ImageButton;

    private lateinit var presenter:MainPresenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this);

        cameraButton = findViewById(R.id.camera_button)

        val recyclerView = findViewById<RecyclerView>(R.id.preview_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MainAdapter()

        cameraButton.setOnClickListener{
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

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            makeCall()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                PERMISSION_CALL
            )
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALL) {
            var access = true
            for (grantResult in grantResults) {
                access = access and (grantResult == PackageManager.PERMISSION_GRANTED)
            }
            if (access) makeCall()
        }
    }

    fun makeCall(){
        val rawProperties =
            RawProperties(resources.openRawResource(R.raw.application)) // getting XML

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(rawProperties.getValue("server")!=null)
            GlobalProperties.serverAddress = rawProperties.getValue("server")!!
            if(rawProperties.getValue("ksite")!=null)
                GlobalProperties.ksiteAddress = rawProperties.getValue("ksite")!!
        }

        GlobalProperties.setFileProperties(FileProperties(File(filesDir, "config.data")))


        if(!UserFactory.getInstance().currentUser.authorization)
        startActivity(Intent(this,AuthorizationView::class.java))

        Log.d("START","Ksite ${GlobalProperties.ksiteAddress}")
        Log.d("START","server ${GlobalProperties.serverAddress}")


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

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
    }

    override fun clickOnCameraButton() {
        startActivity(Intent(this, CreatePhotoActivity::class.java))
    }





}

