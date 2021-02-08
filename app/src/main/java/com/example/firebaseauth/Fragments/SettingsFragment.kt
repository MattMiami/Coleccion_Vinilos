package com.example.firebaseauth.Fragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.firebaseauth.R
import com.example.firebaseauth.Vistas.LoginActivity
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.ByteArrayOutputStream


class SettingsFragment : Fragment() {


    val CAPTURA = 1
    val PICK = 2

    var auth = FirebaseAuth.getInstance().currentUser
    var database =
        FirebaseDatabase.getInstance("https://fir-auth-2021-default-rtdb.europe-west1.firebasedatabase.app/")


    //Creamos una instancia que hace referencia al almacenamiento de Firebase, nos servira para guradar las fotos y acceder a ellas mediante una uri
    var storage = FirebaseStorage.getInstance("gs://fir-auth-2021.appspot.com").getReference()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recuperar los valores del usuario
        //val bundle = arguments?.getBundle("email")
        val email = arguments?.getString("email")
        val password = arguments?.getString("password")


        //guardar datos de las prefs
        val prefs =
            activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                ?.edit()
        prefs?.putString("email", email)
        prefs?.putString("password", password)
        prefs?.apply()




        btSave.setOnClickListener {

            if (!etName.getText().toString().isEmpty() || !etCity.getText().toString().isEmpty()) {
                database.reference.child("USERS").child(auth?.uid!!).child("name")
                    .setValue(etName.text.toString())
                database.reference.child("USERS").child(auth?.uid!!).child("city")
                    .setValue(etCity.text.toString())
                Toast.makeText(getApplicationContext(), "Datos actualizados!", Toast.LENGTH_SHORT)
                    .show()

                etName.setText("")
                etCity.setText("")
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "Para guardar debes rellenar los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        //Infor del usuario
        userPhoto()
        logout()


    }


    //------------------------------------LogOut-------------------------------------------
    private fun logout() {

        btLogout.setOnClickListener {

            val prefs =
                activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                    ?.edit()

            prefs?.clear()
            prefs?.apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()

        }
    }

    //------------------------------------para la foto de perfil-------------------------------------------
    private fun userPhoto() {
        btCamara.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(intent, CAPTURA)
            } catch (e: ActivityNotFoundException) {
                e.message
            }
        }

        btGaleria.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, PICK)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        //Resultado del intent de la galeria
        if (requestCode == PICK && resultCode == Activity.RESULT_OK) {

            //Dialogo de  carga mientras se procesa la foto
            progressBar()

            //Asignamos la uri a la foto del usuario
            val uriaux = data!!.data


            //Creamos una carpeta llamada "fotos" dentro del Storage de Firebase
            val refFilepath =
                storage.child("fotos").child(uriaux!!.lastPathSegment!!)

            /*
                A continuacion, procederemos a subir la foto al Storage de firebase y a cargar la imagen en un ImageView
             */refFilepath.putFile(uriaux).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                refFilepath.downloadUrl
            }.addOnCompleteListener { task ->
                //Escondemos la barra de progreso, actualizamos la foto en la base de datos y por lo tanto la foto de perfil del usuario
                if (task.isSuccessful) {


                    database.reference.child("USERS").child(auth?.uid!!).child("foto")
                        .setValue(task.result.toString())
                    Glide.with(requireContext().applicationContext)
                        .load(task.result.toString())
                        .centerCrop()
                        .fitCenter()
                        .into(ivUserSetting)
                    Toast.makeText(
                        getApplicationContext(),
                        "Foto actualizada!",
                        Toast.LENGTH_SHORT
                    ).show()



                }
                progressBar.visibility = GONE;
            }
        }

        //Resultado del intent de la captura de la foto
        if (requestCode == CAPTURA && resultCode == Activity.RESULT_OK) {

            //Dialogo de  carga mientras se procesa la foto
            progressBar()

            //Obtenemos la imagen de la cámara, la metemos en un byteArray
            val imageBitmap = data?.extras?.get("data") as Bitmap
            ivUserSetting.setImageBitmap(imageBitmap)
            val bitmap = (ivUserSetting.getDrawable() as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val refFilepath = storage.child("perfil").child(data.toString())

            refFilepath!!.putBytes(data).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                refFilepath.downloadUrl
            }.addOnCompleteListener { task ->
                //Escondemos la barra de progreso, actualizamos la foto en la base de datos y por lo tanto la foto de perfil del usuario
                if (task.isSuccessful) {


                    database.reference.child("USERS").child(auth?.uid!!).child("foto")
                        .setValue(task.result.toString())
                    Glide.with(requireContext().applicationContext)
                        .load(task.result.toString())
                        .centerCrop()
                        .fitCenter()
                        .into(ivUserSetting)
                    Toast.makeText(
                        getApplicationContext(),
                        "Foto actualizada!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                progressBar.visibility = GONE;


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun progressBar(){

        progressBar.progress = 200
        progressBar.visibility = VISIBLE;
        progressBar.max = 1000
        val currentProgress = 999
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(2000)
            .start()

    }

}