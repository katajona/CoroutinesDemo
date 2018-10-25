package com.example.katajona.coroutinesdemo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.katajona.coroutinesdemo.R
import com.example.katajona.coroutinesdemo.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.rx2.await
import kotlin.coroutines.CoroutineContext


class CoroutinesFragment : Fragment(), MainNavigationFragment, CoroutineScope {

    lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
    }


    companion object {
        val TAG: String = CoroutinesFragment::class.java.simpleName

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_coroutines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        launch(Dispatchers.IO) {
//           val myTask = async { runLongTask() }
//           showText(myTask.await())
//
//            val myOtherTask=withContext(Dispatchers.IO) {
//                runLongTask()
//            }
//            showText(myOtherTask)
//
//        }

        launch {
            runWithContext()

        }

        launch(Dispatchers.IO) {
            val i1 = async { getTimeWithAsync() }
            val i2 = async { getTimeWithAsync() }
            val i3 = async { getTimeWithAsync() }

            i1.await()
            i2.await()
            i3.await()
        }

        launch {
            retrofit()

        }

    }

    val service = RetrofitFactory.makeRetrofitService()


    suspend fun retrofit() {
        val game = withContext(Dispatchers.IO) {
            service.getBookData().await()
        }
        showText(game.name)
    }


    private suspend fun runWithContext() {
        withContext(Dispatchers.IO)
        {
            getTimeWithContext()
        }
        withContext(Dispatchers.IO)
        {
            getTimeWithContext()
        }
        withContext(Dispatchers.IO)
        {
            getTimeWithContext()
        }
    }


    private fun getTimeWithContext() {
        Thread.sleep(3000)
        Log.d(TAG + " withContext", System.currentTimeMillis().toString())
    }


    private fun getTimeWithAsync() {
        Thread.sleep(3000)
        Log.d(TAG + " async", System.currentTimeMillis().toString())
    }


    private fun showText(text: String) {
        Log.d(TAG, text)
        textView.text = text


    }

    private fun runLongTask(): String {
        Thread.sleep(3000)
        Log.d(TAG, System.currentTimeMillis().toString())
        return "successs"
    }


}
