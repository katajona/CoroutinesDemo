package com.example.katajona.coroutinesdemo.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.katajona.coroutinesdemo.R
import kotlinx.coroutines.Job


class OtherFragment : Fragment(), MainNavigationFragment {


    private val job = Job()


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>



    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_other, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



    }


}
