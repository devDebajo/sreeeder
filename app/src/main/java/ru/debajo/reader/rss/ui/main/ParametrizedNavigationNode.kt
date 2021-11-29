package ru.debajo.reader.rss.ui.main

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import ru.debajo.reader.rss.ui.ext.navigate

interface ParametrizedNavigationNode<T> {

    val route: String

    val parameterKey: String

    fun navigate(navController: NavController, data: T) {
        navController.navigate(route, bundleOf(parameterKey to data))
    }

    fun extract(bundle: Bundle?): T
}


interface StringNavigationNode : ParametrizedNavigationNode<String> {

    override fun extract(bundle: Bundle?): String = bundle?.getString(parameterKey)!!
}

interface ParcelableNavigationNode<T : Parcelable> : ParametrizedNavigationNode<T> {

    override fun extract(bundle: Bundle?): T = bundle?.getParcelable(parameterKey)!!
}
