package com.example.firebaseauth.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseauth.Adaptadores.VinilosAdapter
import com.example.firebaseauth.Modelos.Vinilo
import com.example.firebaseauth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_vinilos.*


class VinilosFragment : Fragment() {

    //Referencias al usuario actual y a su nodo en la base de datos
    var auth = FirebaseAuth.getInstance().currentUser
    var database =
        FirebaseDatabase.getInstance("https://fir-auth-2021-default-rtdb.europe-west1.firebasedatabase.app/")

    var vinilosList = ArrayList<Vinilo>()

    var uriGoogleImages: String = "https://images.google.com/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initrecyclerView()
        addVinilo()


        btSearch.setOnClickListener {

            val uri = Uri.parse(uriGoogleImages)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_vinilos, container, false)
    }


    //Recorremos el nodo de la base de datos donde estan todos los vinilos de ese usuario para mostrarlos en el recycler view
    fun initrecyclerView() {

        database.reference.child("USERS").child(auth?.uid!!).child("Coleccion")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    vinilosList.clear()
                    for (xVinilo in dataSnapshot.children) {
                        val name = xVinilo.child("name").getValue(String::class.java)!!
                        val foto = xVinilo.child("foto").getValue(String::class.java)!!
                        val year = xVinilo.child("year").getValue(String::class.java)!!
                        val genre = xVinilo.child("genre").getValue(String::class.java)!!

                        var vinilo = Vinilo(name, foto, year, genre)

                        vinilosList.add(vinilo)

                    }


                    try {
                        rvVinilos.layoutManager = LinearLayoutManager(context)
                        val adapter = VinilosAdapter(vinilosList)
                        rvVinilos.adapter = adapter
                    } catch (nex: NullPointerException) {

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }


    //Añadimos un vinilo al recycler view y a la base de datos
    fun addVinilo() {
        btAddVinilo.setOnClickListener {


            //Si hay datos en blanco no nos deja guardar
            if (etViniloName.text.isEmpty() || etViniloGenre.text.isEmpty() || etViniloPhoto.text.isEmpty() || !etViniloPhoto.text.toString()
                    .contains("https://") || etViniloYear.text.isEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No dejes campos vacios!" + "\n" + "Asegurate de introducir una URL de imagen correcta",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Si estan todos los datos guardamos un vinilo en la base de datos y refrescamos el recycler view
            else {

                var vinilo = Vinilo(
                    etViniloName.text.toString(),
                    etViniloPhoto.text.toString(),
                    etViniloYear.text.toString(),
                    etViniloGenre.text.toString()
                )

                database.reference.child("USERS").child(auth?.uid!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            database.reference.child("USERS").child(auth?.uid!!).child("Coleccion")
                                .child(vinilo.name).setValue(vinilo)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                vinilosList.add(vinilo)
                initrecyclerView()

                etViniloName.setText("")
                etViniloPhoto.setText("")
                etViniloYear.setText("")
                etViniloGenre.setText("")
                Toast.makeText(context, "Nuevo disco añadido a la coleción", Toast.LENGTH_LONG)
                    .show()


            }

        }

    }

}