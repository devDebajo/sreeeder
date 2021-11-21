package ru.debajo.reader.rss.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.java.KoinJavaComponent

object KoinViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KoinJavaComponent.get(modelClass)
    }
}
