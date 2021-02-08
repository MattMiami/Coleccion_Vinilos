package com.example.firebaseauth.Vistas

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseauth.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    //Para verificar si el user esta conectado o no
    val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Thread.sleep(2000)
        setTheme(R.style.SplashTheme)

        goHome()
    }

    //Si el usuario esta autentificado entrará directamente en la aplicacion, si no tendra que ir a la pantalla de login
    fun goHome() {
        if (authUser.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java)).apply {

            }
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}