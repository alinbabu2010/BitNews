package com.example.demoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demoapp.R


/**
 * A simple [Fragment] subclass for displaying news
 */
class NewsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.actionBar?.setDisplayShowTitleEnabled(true)
        activity?.title = "News"

        // Inflate the layout for this fragment
        val inflatedView = inflater.inflate(R.layout.fragment_news, container, false)

        return inflatedView
    }

}