package com.example.katajona.coroutinesdemo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.ListFragment
import android.text.TextUtils.replace



inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}