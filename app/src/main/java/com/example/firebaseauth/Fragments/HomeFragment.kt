package com.example.firebaseauth.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.firebaseauth.Modelos.Users
import com.example.firebaseauth.Modelos.Vinilo
import com.example.firebaseauth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_vinilo.view.*
import java.lang.NullPointerException


class HomeFragment : Fragment() {

    //Autentificacion
    var auth = FirebaseAuth.getInstance().currentUser

    //Referencia a la base de datos
    var database =
        FirebaseDatabase.getInstance("https://fir-auth-2021-default-rtdb.europe-west1.firebasedatabase.app/")
    var vinilosList = ArrayList<Vinilo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //El usuario por defecto
        var user = Users(
            auth?.displayName.toString(),
            "https://firebasestorage.googleapis.com/v0/b/fir-auth-2021.appspot.com/o/userPink.jpg?alt=media&token=83553354-2dd1-4ee4-a6a9-25c6debb3a9d",
            "",
            auth?.email.toString()
        )

        database.reference.child("USERS").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                //Si el usuario no existe, lo crea
                if (!dataSnapshot.hasChild(auth?.uid.toString())) {
                    database.reference.child("USERS").child(auth?.uid!!).setValue(user)

                    //Si existe recoge los valores y los muestra en la informacion del usuario
                } else {
                    var name: String? =
                        dataSnapshot.child(auth?.uid!!).child("name").getValue(String::class.java)
                    var city: String? =
                        dataSnapshot.child(auth?.uid!!).child("city").getValue(String::class.java)
                    var email: String? =
                        dataSnapshot.child(auth?.uid!!).child("email").getValue(String::class.java)
                    var foto: String? =
                        dataSnapshot.child(auth?.uid!!).child("foto").getValue(String::class.java)

                    try {
                        tvName?.text = name.toString()
                        tvCity?.text = city.toString()
                        tvEmail.text = email
                        Glide.with(view.ivUser.context).load(foto).into(view.ivUser)
                    } catch (nex: NullPointerException) {
                    }

                }


            }


            override fun onCancelled(error: DatabaseError) {

                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })


        database.reference.child("USERS")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Para obtener el número de Vinilos en la coleccion del usuario
                    vinilosList.clear()
                    for (xVinilo in dataSnapshot.child(auth?.uid!!).child("Coleccion").children) {
                        val name = xVinilo.child("name").getValue(String::class.java)!!
                        val foto = xVinilo.child("foto").getValue(String::class.java)!!
                        val year = xVinilo.child("year").getValue(String::class.java)!!
                        val genre = xVinilo.child("genre").getValue(String::class.java)!!

                        var vinilo = Vinilo(name, foto, year, genre)

                        vinilosList.add(vinilo)

                    }

                    try {
                        tvNumeroVinilos.text = vinilosList.size.toString()

                    } catch (nex: NullPointerException) {
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


    }


}





