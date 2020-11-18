package com.example.demoapp.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.demoapp.R
import com.example.demoapp.utils.loadJSONFromAsset
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A bottom sheet dialog fragment for filtering news
 */
class FilterDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.news_filter,
            container, false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting news data and get the source name from it.
        val fileData: String? = activity?.assets?.open("news.json")?.readBytes()?.let { String(it) }
        val news = loadJSONFromAsset(fileData)
        val article = news.articles
        val source = mutableSetOf<String>()
        article.forEach { source.add(it.source.name) }


        // Adding radio button based on the source set
        val sourceRadioGroup: RadioGroup = view.findViewById(R.id.source_radioGroup)
        for (i in source.indices) {
            val radioButton = RadioButton(context)
            radioButton.text = source.toTypedArray()[i]
            radioButton.id = View.generateViewId()
            sourceRadioGroup.addView(radioButton)
        }

        // Initializing data picker dialog and inflating date picker on clicking
        val dateView: TextView = view.findViewById(R.id.publish_datepicker)
        val date: Calendar = Calendar.getInstance()
        val datePicker =
            context?.let {
                DatePickerDialog(
                    it,
                    { view, year, month, dayOfMonth ->
                        val updateDate = Calendar.getInstance()
                        updateDate.set(year, month, dayOfMonth)
                        val dateToSet = "$year-$month-$dayOfMonth"
                        dateView.text = dateToSet
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                )
            }
        dateView.setOnClickListener { datePicker?.show() }
    }

}