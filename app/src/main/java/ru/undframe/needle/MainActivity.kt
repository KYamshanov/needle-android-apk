package ru.undframe.needle

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.undframe.needle.mvp.MainPresenter
import ru.undframe.needle.mvp.MainView
import ru.undframe.needle.mvp.Presenter


class MainActivity : AppCompatActivity(),MainView {


    private lateinit var cameraButton:ImageButton;

    private lateinit var presenter:Presenter

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
            onClickPhoto(it)
        }


    }

    val TYPE_PHOTO = 1
    val TYPE_VIDEO = 2

    val REQUEST_CODE_PHOTO = 1
    val REQUEST_CODE_VIDEO = 2

    fun onClickPhoto(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      /*  intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO))
        registerForActivityResult()*/
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
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
        presenter.openCamera()
    }

}

