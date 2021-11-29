package ru.debajo.reader.rss.ui.main

import androidx.navigation.NavController

interface NavigationNode {
    val route: String

    fun navigate(navController: NavController) {
        navController.navigate(route)
    }
}
