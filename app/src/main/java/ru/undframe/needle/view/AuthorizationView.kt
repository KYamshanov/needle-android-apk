package ru.undframe.needle.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.undframe.needle.MainActivity
import ru.undframe.needle.R
import ru.undframe.needle.utils.AuthStatus
import ru.undframe.needle.utils.UserFactory

class AuthorizationView : AppCompatActivity() {


    private lateinit var loginTextView: TextView
    private lateinit var passwordTextView:TextView
    private lateinit var savePasswordCallback: CheckBox
    private lateinit var error: TextView

    private lateinit var authButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autorization_view)

        UserFactory.getInstance()
            .refreshSavedUser {
                startActivity(Intent(this,MainActivity::class.java))
            }

        authButton = findViewById(R.id.authButton)
        loginTextView = findViewById(R.id.login)
        passwordTextView = findViewById(R.id.password)
        savePasswordCallback = findViewById(R.id.auth_savepassword)
        error = findViewById(R.id.auth_error_info)


        authButton.setOnClickListener{
            val userName:String = loginTextView.text.toString()
            val password:String = passwordTextView.text.toString()

            UserFactory.getInstance().authCurrentUser(
                userName,
                password,
                savePasswordCallback.isChecked
            ) { user ->
                if (user.authStatus != AuthStatus.SUCCESSFUL_AUTHORIZATION) {
                    error.visibility = View.VISIBLE
                } else
                    startActivity(Intent(this,MainActivity::class.java))
            }
        }

    }
}