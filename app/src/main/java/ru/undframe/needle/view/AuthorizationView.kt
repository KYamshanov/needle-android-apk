package ru.undframe.needle.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.undframe.needle.MainActivity
import ru.undframe.needle.R
import ru.undframe.needle.presenters.AuthorizationPresenter

class AuthorizationView : AppCompatActivity(),AuthView {


    private lateinit var loginTextView: TextView
    private lateinit var passwordTextView:TextView
    private lateinit var savePasswordCallback: CheckBox
    private lateinit var error: TextView
    private lateinit var authButton: Button


    private lateinit var authorizationPresenter: AuthorizationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autorization_view)

        authButton = findViewById(R.id.authButton)
        loginTextView = findViewById(R.id.login)
        passwordTextView = findViewById(R.id.password)
        savePasswordCallback = findViewById(R.id.auth_savepassword)
        error = findViewById(R.id.auth_error_info)

        authorizationPresenter = AuthorizationPresenter(this)

        authorizationPresenter.uploadFromSave()

        authButton.setOnClickListener{
            val userName:String = loginTextView.text.toString()
            val password:String = passwordTextView.text.toString()
            val savePassword: Boolean = savePasswordCallback.isChecked

            authorizationPresenter.authUser(userName, password, savePassword)
        }
    }

    override fun showError() {
        error.visibility = View.VISIBLE
    }

    override fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun openAuthorizationView() {
    }

    override fun getContext(): Context {
        return this
    }
}