package com.example.nsu_vk_project

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException

class WelcomeActivity : AppCompatActivity() {

    private lateinit var authLauncher: ActivityResultLauncher<Collection<VKScope>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (VK.isLoggedIn()) {
            startActivity( Intent(this,MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_welcome)

        authLauncher = VK.login(this) { result : VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> onLogin()
                is VKAuthenticationResult.Failed -> onLoginFailed(result.exception)
            }
        }

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))
        }
    }

    private fun onLogin() {
        startActivity( Intent(this,MainActivity::class.java))
        finish()
    }

    private fun onLoginFailed(exception: VKAuthException) {
        if (!exception.isCanceled) {
            val descriptionResource =
                if (exception.webViewError == WebViewClient.ERROR_HOST_LOOKUP) R.string.message_connection_error
                else R.string.message_unknown_error
            AlertDialog.Builder(this@WelcomeActivity)
                .setMessage(descriptionResource)
                .setPositiveButton(R.string.vk_retry) { _, _ ->
                    authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

}