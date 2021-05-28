package ru.undframe.needle.utils

import ru.undframe.needle.model.User
import ru.undframe.needle.tasks.CheckAliveTokenTask
import ru.undframe.needle.view.BaseView

class AuthorizationUserTask(private val baseView: BaseView) {
    var task: BiNConsumer<User, String>? = null
    var exceptionHandler: NConsumer<Exception>? = null
    var openAuthView: Boolean = true

    fun execute() {
        try {
            val currentUser = UserFactory.getInstance().currentUser

            if (currentUser.accessToken != null)
                CheckAliveTokenTask(currentUser.accessToken!!) { it ->
                    println("STATUS $it")
                    if (it == ResponseStatus.TOKEN_IS_ALIVE) {
                        task?.accept(currentUser,currentUser.accessToken)
                    } else {
                        UserFactory.getInstance().refreshCurrentUser {
                            if (it.authorization)
                                task?.accept(currentUser,currentUser.accessToken)
                            else
                                baseView.openAuthorizationView()
                        }
                    }

                }.execute()
            else
                baseView.openAuthorizationView()
        } catch (e: java.lang.Exception) {
            exceptionHandler?.accept(e)
            if (openAuthView)
                baseView.openAuthorizationView()
        }
    }


}