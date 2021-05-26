package ru.undframe.needle.mvp

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File


class MainPresenter( private val view:MainView) : Presenter{

    private val TAKE_PICTURE = 1
    private var imageUri: Uri? = null

    override fun openCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg")
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Uri.fromFile(photo)
        )

    }
}