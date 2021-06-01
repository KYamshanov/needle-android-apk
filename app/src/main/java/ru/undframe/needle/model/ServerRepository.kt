package ru.undframe.needle.model

import ru.undframe.needle.model.BaseServerRepository.Companion.getInstance
import java.io.File

interface ServerRepository {
    suspend fun sendFileToServer(token: String, file: File)

    companion object {
        val baseInstance: ServerRepository
            get() = getInstance()
    }
}