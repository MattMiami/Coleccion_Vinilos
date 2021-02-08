package com.example.firebaseauth.Adaptadores

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseauth.Modelos.Vinilo
import com.example.firebaseauth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
        holder.viniloName.setOnClickListener{

            val builder = AlertDialog.Builder(holder.viniloName.context)
            builder.setTitle("Eliminar vinilo")
            builder.setMessage("¿Quieres eliminar este vinilo de la lista?")
            builder.setPositiveButton("SI") { dialog, which ->

                database.reference.child("USERS").child(auth?.uid!!).child("Coleccion").child(holder.viniloName.text.toString()).removeValue()
                listaVinilos.removeAt(holder.adapterPosition)
                Toast.makeText(holder.viniloName.context,"Eliminado correctamente", Toast.LENGTH_SHORT).show()

            }
            builder.setNegativeButton("NO") { dialog, which ->

            }
            builder.show()
        }

    }



    class VinilosHolder(val view: View) : RecyclerView.ViewHolder(view){

        val viniloName: TextView = view.findViewById(R.id.viniloName)

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