package com.example.firebaseauth.Adaptadores

import android.app.AlertDialog
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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_favs.view.*
import kotlinx.android.synthetic.main.item_vinilo.view.*

class FavoritosAdapter(val listaFavoritos: ArrayList<Vinilo>) :
    RecyclerView.Adapter<FavoritosAdapter.FavoritosHolder>() {

    //Referencias al user y a la base de datos
    var auth = FirebaseAuth.getInstance().currentUser
    var database =
        FirebaseDatabase.getInstance("https://fir-auth-2021-default-rtdb.europe-west1.firebasedatabase.app/")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return FavoritosHolder(layoutInflater.inflate(R.layout.item_favs, parent, false))

    }


    override fun getItemCount(): Int = listaFavoritos.size



    override fun onBindViewHolder(holder: FavoritosAdapter.FavoritosHolder, position: Int) {
        holder.render(listaFavoritos[position])

        holder.viniloName.setOnClickListener {

            val builder = AlertDialog.Builder(holder.viniloName.context)
            builder.setTitle("Eliminar de favoritos vinilo")
            builder.setMessage("¿Quieres eliminar este vinilo de la lista de favoritos?")
            builder.setPositiveButton("SI") { dialog, which ->

                database.reference.child("USERS").child(auth?.uid!!).child("Favoritos")
                    .child(holder.viniloName.text.toString()).removeValue()
                listaFavoritos.removeAt(holder.adapterPosition)
                Toast.makeText(
                    holder.viniloName.context,
                    "Eliminado de favoritos",
                    Toast.LENGTH_SHORT
                ).show()

            }
            builder.setNegativeButton("NO") { dialog, which ->

            }
            builder.show()
        }

    }





    class FavoritosHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val viniloName: TextView = view.findViewById(R.id.favName)
        val viniloGenre: TextView = view.findViewById(R.id.favGenre)
        val viniloYear: TextView = view.findViewById(R.id.favYear)
        val viniloFoto: ImageView = view.findViewById(R.id.ivFav)

        fun render(vinilo: Vinilo) {
            view.favName.text = vinilo.name
            view.favYear.text = vinilo.year
            view.favGenre.text = vinilo.genre
            Glide.with(view.ivFav.context)
                .load(vinilo.foto)
                .into(view.ivFav)


        }

    }


}