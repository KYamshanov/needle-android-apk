package ru.undframe.needle.presenters

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.undframe.needle.model.ServerRepository
import ru.undframe.needle.utils.authUserTask
import ru.undframe.needle.view.BaseView
import java.io.File
import java.util.*

class CreatePhotoPresenter(
    private var baseView: BaseView,
    private var serverRepository: ServerRepository
) {


    private lateinit var directory: File
    private var savedImgFile: File? = null


    init {
        createDirectory()
    }

    fun uploadImage() {
        if (savedImgFile != null)
            authUserTask(baseView) { _, s ->
                CoroutineScope(Dispatchers.IO).launch {
                    serverRepository.sendFileToServer(s, savedImgFile!!)
                }
            }
    }

    fun hasSavedImage():Boolean{
        return savedImgFile!=null
    }


    fun generateNewFile(): Uri? {
        val file = File(
            directory.path + "/" + "photo_"
                    + UUID.randomUUID() + ".jpg"
        )
        savedImgFile = file
        val imageUri = FileProvider.getUriForFile(
            baseView.context, "ru.undframe.needle.provider", file
        )
        Log.d("generate file", "fileName = $file")

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

