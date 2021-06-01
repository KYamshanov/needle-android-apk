package ru.undframe.needle.utils

import ru.undframe.needle.model.User
import ru.undframe.needle.view.BaseView

inline fun authUserTask(baseView: BaseView, crossinline action: (User, String) -> Unit) {
    AuthorizationUserTask(baseView).apply {
        task = BiNConsumer { u, token ->
            action(u, token)
        }
    }.execute()
}