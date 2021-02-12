package com.example.firebaseauth.Adaptadores

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseauth.Modelos.Vinilo
import com.example.firebaseauth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_vinilos.*
import kotlinx.android.synthetic.main.item_vinilo.view.*


class VinilosAdapter(val listaVinilos: ArrayList<Vinilo>) :
    RecyclerView.Adapter<VinilosAdapter.VinilosHolder>() {

    //Referencias al user y a la base de datos
    var auth = FirebaseAuth.getInstance().currentUser
    var database =
        FirebaseDatabase.getInstance("https://fir-auth-2021-default-rtdb.europe-west1.firebasedatabase.app/")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VinilosHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return VinilosHolder(layoutInflater.inflate(R.layout.item_vinilo, parent, false))

    }


    override fun getItemCount(): Int = listaVinilos.size


    override fun onBindViewHolder(holder: VinilosHolder, position: Int) {
        holder.itemView.isLongClickable = true;
        holder.render(listaVinilos[position])

        //Para eliminar un item de la lista y de la base de datos
        holder.viniloName.setOnClickListener {

            val builder = AlertDialog.Builder(holder.viniloName.context)
            builder.setTitle("Eliminar vinilo")
            builder.setMessage("¿Quieres eliminar este vinilo de la lista?")
            builder.setPositiveButton("SI") { dialog, which ->

                database.reference.child("USERS").child(auth?.uid!!).child("Coleccion")
                    .child(holder.viniloName.text.toString()).removeValue()
                listaVinilos.removeAt(holder.adapterPosition)
                Toast.makeText(
                    holder.viniloName.context,
                    "Eliminado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

            }
            builder.setNegativeButton("NO") { dialog, which ->

            }
            builder.show()
        }

        //Para añadir los items a una lista de favoritos en la base de datos, Cogemos el valor de la posicion del holder y lo metemos en una lista

        holder.addToFav.setOnClickListener {


            database.reference.child("USERS").child(auth?.uid!!).child("Coleccion")
                .child(holder.viniloName.text.toString())
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val name = dataSnapshot.child("name").getValue(String::class.java)!!
                        val foto = dataSnapshot.child("foto").getValue(String::class.java)!!
                        val year = dataSnapshot.child("year").getValue(String::class.java)!!
                        val genre = dataSnapshot.child("genre").getValue(String::class.java)!!

                        var vinilo = Vinilo(name, foto, year, genre)

                        database.reference.child("USERS").child(auth?.uid!!).child("Favoritos")
                            .child(holder.viniloName.text.toString()).setValue(vinilo)

                        Toast.makeText(
                            holder.viniloName.context,
                            "Agregado a favoritos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


        }

    }


    class VinilosHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val viniloName: TextView = view.findViewById(R.id.viniloName)
        val addToFav: ImageButton = view.findViewById(R.id.addToFav)
        val viniloGenre: TextView = view.findViewById(R.id.viniloGenre)
        val viniloYear: TextView = view.findViewById(R.id.viniloYear)
        val viniloFoto: ImageView = view.findViewById(R.id.ivVinilo)

        fun render(vinilo: Vinilo) {
            view.viniloName.text = vinilo.name
            view.viniloYear.text = vinilo.year
            view.viniloGenre.text = vinilo.genre
            Glide.with(view.ivVinilo.context)
                .load(vinilo.foto)
                .into(view.ivVinilo)


        }

    }


}