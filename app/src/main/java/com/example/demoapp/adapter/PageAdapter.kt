package com.example.demoapp.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demoapp.R
import com.example.demoapp.fragments.dashboard.FavouritesFragment
import com.example.demoapp.fragments.dashboard.NewsFragment
import com.example.demoapp.fragments.dashboard.ProfileFragment

/**
 * Adapter class for ViewPager
 */
class PageAdapter(fragment: FragmentManager, private val tabCount: Int) :
    FragmentStatePagerAdapter(fragment, tabCount) {

    private val tabTitles = arrayOf(
        Resources.getSystem().getString(R.string.news_title),
        Resources.getSystem().getString(R.string.favourite_title),
        Resources.getSystem().getString(R.string.profile_text)
    )

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