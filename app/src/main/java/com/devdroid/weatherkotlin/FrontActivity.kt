package com.devdroid.weatherkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_front.*

class FrontActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front)

        btnSubmit.setOnClickListener {
            if(edtName.length()==0){
                Toast.makeText(applicationContext,"Please enter your name",Toast.LENGTH_SHORT).show()
            } else{
                sharedPreferences = applicationContext.getSharedPreferences("temperature",
                    Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("name",edtName.text.toString())
                editor.commit()
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
