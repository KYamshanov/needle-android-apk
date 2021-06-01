package ru.undframe.needle.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.undframe.needle.R

class NoAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_access)
    }
}