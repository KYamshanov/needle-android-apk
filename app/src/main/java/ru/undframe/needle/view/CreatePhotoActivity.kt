package ru.undframe.needle.view

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


class CreatePhotoActivity : AppCompatActivity() {

    private val LOG_TAG = "Create image log"


    private lateinit var directory: File
    private lateinit var ivPhoto: ImageView;
    private lateinit var createPhotoPresenter: CreatePhotoPresenter
    private lateinit var uploadPhoto: Button
    private var savedImgFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_photo)

        createDirectory()
        ivPhoto = findViewById(R.id.create_photot_vimgview);
        uploadPhoto = findViewById(R.id.upload_photo)
        createPhotoPresenter = CreatePhotoPresenter(
            ServerRepository.getBaseInstance()
        )

        launchCamera()

        uploadPhoto.setOnClickListener {
            if (savedImgFile != null)
                createPhotoPresenter.uploadImage(savedImgFile!!)
        }

    }

    private fun launchCamera() {
        val generateFileUri = generateFileUri()
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, generateFileUri)
                ivPhoto.setImageBitmap(bitmap)
            }
        }.launch(generateFileUri)
    }


    private fun generateFileUri(): Uri? {
        val file = File(
            directory.path + "/" + "photo_"
                    + System.currentTimeMillis() + ".jpg"
        )
        savedImgFile = file
        val imageUri = FileProvider.getUriForFile(
            this, "ru.undframe.needle.provider", file
        )
        Log.d(LOG_TAG, "fileName = $file")

        return imageUri
    }

    private fun createDirectory() {
        directory = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyFolder"
        )
        if (!directory.exists()) directory.mkdirs()
    }

}