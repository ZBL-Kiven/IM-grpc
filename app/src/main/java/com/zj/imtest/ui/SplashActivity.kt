package com.zj.imtest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zj.ccIm.core.IMHelper
import com.zj.imtest.BaseApp
import com.zj.imtest.IMConfig
import com.zj.imtest.R

class SplashActivity : AppCompatActivity() {

    private lateinit var et: EditText
    private lateinit var tvLogin: TextView
    private lateinit var tvClear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        et = findViewById(R.id.splash_et)
        tvLogin = findViewById(R.id.splash_tv_login)
        tvClear = findViewById(R.id.splash_tv_clear)
        if (BaseApp.hasInitChat) {
            gotoMain()
        }
        setListener()
    }

    private fun setListener() {
        et.setText("${IMConfig.defaultTestUid}")
        tvLogin.setOnClickListener {
            val le = BaseApp.getLoginUisFromSp()
            val cur = et.text?.toString()?.toInt() ?: -1
            if (le <= 0 || cur != le) {
                val uid = et.text?.toString()
                if (uid.isNullOrEmpty()) {
                    Toast.makeText(this, "Input a uid at first!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    BaseApp.commitNewUid(uid.toInt())
                }
            }
            gotoMain()
        }

        tvClear.setOnClickListener {
            IMHelper.loginOut()
            BaseApp.clear()
        }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}