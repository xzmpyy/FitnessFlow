package com.example.zhangjie.fitnessflow.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.zhangjie.fitnessflow.R
import kotlin.system.exitProcess

class PrivateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)
        this.findViewById<Button>(R.id.quit).setOnClickListener {
            exitProcess(0)
        }
        this.findViewById<Button>(R.id.agree).setOnClickListener {
            val share = this.getSharedPreferences("Private", Context.MODE_PRIVATE)
            share.edit().putBoolean("PrivateFlag", true).apply()
            val intent = Intent(this, IndexActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
