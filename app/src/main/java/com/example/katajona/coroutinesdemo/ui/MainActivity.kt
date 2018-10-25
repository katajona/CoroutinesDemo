package com.example.katajona.coroutinesdemo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.katajona.coroutinesdemo.R
import com.example.katajona.coroutinesdemo.consume
import com.example.katajona.coroutinesdemo.inTransaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_ID = R.id.fragment_container
    }

    private lateinit var currentFragment: MainNavigationFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_coroutines -> consume {
                    replaceFragment(CoroutinesFragment())
                }
                R.id.navigation_other -> consume {
                    replaceFragment(OtherFragment())
                }
                else -> false
            }
        }
        navigation.selectedItemId= R.id.navigation_coroutines
    }

    private fun <F> replaceFragment(fragment: F) where F : Fragment, F : MainNavigationFragment {
        supportFragmentManager.inTransaction {
            currentFragment = fragment
            replace(FRAGMENT_ID, fragment)
        }
    }

}
