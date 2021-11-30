package ru.debajo.reader.rss.ui.main.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import ru.debajo.reader.rss.ui.ext.navigate

interface NavigationNode<T> {

    val route: String

    val parameterKey: String

    fun navigate(navController: NavController, data: T) {
        navController.navigate(route, bundleOf(parameterKey to data))
    }

    fun extract(bundle: Bundle?): T
}

interface UnitNavigationNode : NavigationNode<Unit> {

    override val parameterKey: String
        get() = ""

    override fun extract(bundle: Bundle?): Unit = Unit

    override fun navigate(navController: NavController, data: Unit) {
        navigate(navController)
    }

    fun navigate(navController: NavController) {
        navController.navigate(route)
    }
}

interface StringNavigationNode : NavigationNode<String> {

    override fun extract(bundle: Bundle?): String = bundle?.getString(parameterKey)!!
}

interface ParcelableNavigationNode<T : Parcelable> : NavigationNode<T> {

    override fun extract(bundle: Bundle?): T = bundle?.getParcelable(parameterKey)!!
}
