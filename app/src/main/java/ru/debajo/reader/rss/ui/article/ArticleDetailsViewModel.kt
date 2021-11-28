package ru.debajo.reader.rss.ui.article

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle

class ArticleDetailsViewModel : BaseViewModel() {

    private val contentMutable: MutableStateFlow<Document?> = MutableStateFlow(null)
    val content: StateFlow<Document?> = contentMutable

    fun prepare(article: UiArticle) {
        launch {
            contentMutable.value = parseHtml(article.contentHtml)
        }
    }

    private suspend fun parseHtml(html: String?): Document? {
        html ?: return null
        return withContext(Default) { Jsoup.parse(html) }
    }
}
