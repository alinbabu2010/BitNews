package com.example.demoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demoapp.ui.fragments.dashboard.FavouritesFragment
import com.example.demoapp.ui.fragments.dashboard.NewsFragment
import com.example.demoapp.ui.fragments.dashboard.ProfileFragment

/**
 * Adapter class for ViewPager
 */
class PageAdapter(fragment: FragmentManager, private val tabCount: Int, private val tabTitles: Array<String>) :
    FragmentStatePagerAdapter(fragment, tabCount) {

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getItem(position: Int): Fragment {
        lateinit var fragment: Fragment
        when (position) {
            0 -> fragment = NewsFragment()
            1 -> fragment = FavouritesFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment
    }
}