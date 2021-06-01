package ru.undframe.needle.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.undframe.needle.model.User
import ru.undframe.needle.tasks.CheckAliveTokenTask
import ru.undframe.needle.view.BaseView

class AuthorizationUserTask(private val baseView: BaseView) {
    var task: BiNConsumer<User, String>? = null
    var exceptionHandler: NConsumer<Exception>? = null
    var openAuthView: Boolean = true

    fun execute() {
        try {
            val currentUser = UserFactory.getInstance().getCurrentUser()

            if (currentUser.accessToken != null)

                CoroutineScope(Dispatchers.IO).launch {
                    val result = CheckAliveTokenTask(currentUser.accessToken!!).check()

                    if (result == ResponseStatus.TOKEN_IS_ALIVE) {
                        task?.accept(currentUser,currentUser.accessToken)
                    } else {
                        UserFactory.getInstance().refreshCurrentUser {
                            if (it.isAuthorization())
                                task?.accept(currentUser,currentUser.accessToken)
                            else
                                baseView.openAuthorizationView()
                        }
                    }

                }

            else
                baseView.openAuthorizationView()
        } catch (e: java.lang.Exception) {
            exceptionHandler?.accept(e)
            if (openAuthView)
                baseView.openAuthorizationView()
        }
    }


}