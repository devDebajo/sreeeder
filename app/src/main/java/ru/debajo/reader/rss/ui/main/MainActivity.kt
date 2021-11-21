package ru.debajo.reader.rss.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.debajo.reader.rss.data.remote.RssLoader
import ru.debajo.reader.rss.ui.theme.SreeeederTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val reader: RssLoader by inject(RssLoader::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SreeeederTheme {
                Surface(color = MaterialTheme.colors.background) {
                }
            }
        }

        lifecycleScope.launch(IO) {
            val channel = reader.loadChannel("https://blog.jetbrains.com/feed")
            Timber.d(channel.toString())
        }
    }
}
