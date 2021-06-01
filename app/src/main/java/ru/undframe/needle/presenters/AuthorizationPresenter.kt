package ru.undframe.needle.presenters

import ru.undframe.needle.utils.ResponseStatus
import ru.undframe.needle.utils.UserFactory
import ru.undframe.needle.view.AuthView

class AuthorizationPresenter(private var view: AuthView) {

    fun uploadFromSave() {
        UserFactory.getInstance().refreshSavedUser {
            view.openMainActivity()
        }
    }

    fun authUser(userName: String, password: String, savePassword: Boolean) {
        UserFactory.getInstance().authCurrentUser(
            userName,
            password,
            savePassword
        ) { user ->
            if (user.authStatus != ResponseStatus.SUCCESSFUL_AUTHORIZATION) {
                view.showError()
            } else
                view.openMainActivity()
        }
    }


}