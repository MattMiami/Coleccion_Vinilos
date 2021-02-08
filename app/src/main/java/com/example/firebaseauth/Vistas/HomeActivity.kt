package com.example.firebaseauth.Vistas

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauth.Adaptadores.ViewPagerAdapter
import com.example.firebaseauth.Fragments.VinilosFragment
import com.example.firebaseauth.Fragments.HomeFragment
import com.example.firebaseauth.Fragments.SettingsFragment
import com.example.firebaseauth.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT



        setUptTabs()

    }


    //Configuramos la navegacion entre fragments del Tabbed Activity
    private fun setUptTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(VinilosFragment(), "Collection")
        adapter.addFragment(SettingsFragment(), "Settings")

        viewPager.adapter = adapter

        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_person_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_library_music_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_settings_24)


    }


}