package ru.undframe.needle.presenters

import ru.undframe.needle.model.ServerRepository
import java.io.File

class CreatePhotoPresenter(private var serverRepository: ServerRepository){

    fun uploadImage(file:File){
        serverRepository.sendFileToServer(file)
    }

}