package ru.undframe.needle.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import ru.undframe.needle.R
import ru.undframe.needle.model.ServerRepository
import ru.undframe.needle.presenters.CreatePhotoPresenter
import ru.undframe.needle.utils.ImageFilePathUtils
import java.io.File


class SavePhotoActivity : AppCompatActivity(),BaseView {

    private val LOG_TAG = "Create image log"


    private lateinit var ivPhoto: ImageView;
    private lateinit var createPhotoPresenter: CreatePhotoPresenter
    private lateinit var uploadPhoto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_photo)

        createPhotoPresenter = CreatePhotoPresenter(
            this,ServerRepository.baseInstance
        )

        ivPhoto = findViewById(R.id.create_photot_vimgview);
        uploadPhoto = findViewById(R.id.upload_photo)


        launchCamera()

        uploadPhoto.setOnClickListener {
                createPhotoPresenter.uploadImage()
        }

    }

    private fun launchCamera() {
        val generateFileUri = createPhotoPresenter.generateNewFile()
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, generateFileUri)
                ivPhoto.setImageBitmap(bitmap)
            }
        }.launch(generateFileUri)
    }


    override fun openAuthorizationView() {
        startActivity(Intent(this,AuthorizationView::class.java))
    }

    override fun getContext(): Context {
        return this
    }
    override fun openNoAccessActivity() {
        startActivity(Intent(this, NoAccessActivity::class.java))
    }

    override fun closeActivity() {
        finish()
    }

}