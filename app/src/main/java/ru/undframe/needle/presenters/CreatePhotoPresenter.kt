package ru.undframe.needle.presenters

import ru.undframe.needle.model.ServerRepository
import ru.undframe.needle.utils.AuthorizationUserTask
import ru.undframe.needle.utils.BiNConsumer
import ru.undframe.needle.utils.UserFactory
import ru.undframe.needle.view.BaseView
import java.io.File

class CreatePhotoPresenter(private var baseView: BaseView,private var serverRepository: ServerRepository){

    fun uploadImage(file:File){

        AuthorizationUserTask(baseView).apply {

            task = BiNConsumer { _, token ->
                serverRepository.sendFileToServer(token,file)
            }

        }.execute()

    }

}