package com.example.demoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.demoapp.ui.fragments.dashboard.FavouritesFragment
import com.example.demoapp.ui.fragments.dashboard.NewsFragment
import com.example.demoapp.ui.fragments.dashboard.ProfileFragment

/**
 * Adapter class for ViewPager
 */
class PageAdapter(
    fragment: FragmentManager,
    lifeCycle: Lifecycle,
    private val tabCount: Int
) : FragmentStateAdapter(fragment, lifeCycle) {

    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment
        when (position) {
            0 -> fragment = NewsFragment()
            1 -> fragment = FavouritesFragment()
            2 -> fragment = ProfileFragment()
        }
        return fragment
    }

}