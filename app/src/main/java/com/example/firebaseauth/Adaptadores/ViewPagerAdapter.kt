package com.example.firebaseauth.Adaptadores

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import java.util.ArrayList

class ViewPagerAdapter (supportFragmentManager: FragmentManager):FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val fragmentList = ArrayList<Fragment>()
    private val fragmentNameList = ArrayList<String>()



    override fun getItem(position: Int): Fragment {
       return fragmentList[position]
    }

    override fun getCount(): Int {
        return  fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentNameList[position]
    }



    fun addFragment(fragment: Fragment, tittle: String){

        fragmentList.add(fragment)
        fragmentNameList.add(tittle)


    }

}