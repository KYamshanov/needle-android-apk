package ru.undframe.needle.model

import ru.undframe.needle.tasks.UploadFileTask
import java.io.File


class BaseServerRepository: ServerRepository{


    override fun sendFileToServer(token:String,file:File) {
        UploadFileTask(token,file){}.execute()
    }

    companion object {

        private var instance:BaseServerRepository? = null

        fun getInstance():ServerRepository {
            if(instance ==null)
                instance = BaseServerRepository()
            return instance!!
        }
    }

}