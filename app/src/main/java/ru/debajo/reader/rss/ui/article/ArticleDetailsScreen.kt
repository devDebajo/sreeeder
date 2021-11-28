package ru.debajo.reader.rss.ui.article

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import timber.log.Timber

const val ArticleDetailsRoute = "ArticleDetails"
private const val ArticleDetailsRouteArticleParam = "article"

fun channelArticlesRouteParams(article: UiArticle): Bundle {
    return bundleOf(ArticleDetailsRouteArticleParam to article)
}

fun extractUiArticle(bundle: Bundle?): UiArticle {
    return bundle?.getParcelable(ArticleDetailsRouteArticleParam)!!
}

@Composable
@ExperimentalMaterial3Api
fun ArticleDetailsScreen(article: UiArticle) {
    val viewModel: ArticleDetailsViewModel = diViewModel()
    LaunchedEffect(key1 = article, block = {
        viewModel.prepare(article)
    })

    Scaffold {
        val content by viewModel.content.collectAsState()
        content?.let { RenderHtml(it) }
    }
}

@Composable
fun RenderHtml(article: Document) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        for (child in article.body().children()) {
            RenderBlock(child)
        }
    }
}

@Composable
fun RenderBlock(block: Element) {
    val name = block.tag().name
    Timber.d("yopta $name, childrenCount: ${block.childrenSize()}")
    when (name) {
        "head" -> RenderHtmlHeadTag(block)
        "p" -> RenderHtmlPTag(block)
        "h1" -> RenderHtmlHeadingTag(block, 1)
        "h2" -> RenderHtmlHeadingTag(block, 2)
        "h3" -> RenderHtmlHeadingTag(block, 3)
        "h4" -> RenderHtmlHeadingTag(block, 4)
        "h5" -> RenderHtmlHeadingTag(block, 5)
        "h6" -> RenderHtmlHeadingTag(block, 6)
        "ul" -> RenderHtmlUlTag(block)
        else -> Timber.d("yopta unknown tag: $name")
    }
}

@Composable
fun RenderHtmlUlTag(block: Element) {
    Column {
        for (li in block.children().filter { it.tagName() == "li" }) {
            RenderHtmlLiTag(li)
        }
    }
}

@Composable
fun RenderHtmlLiTag(li: Element) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LocalContentColor.current)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(li.ownText())
    }
}

@Composable
fun RenderHtmlHeadingTag(heading: Element, headingFactor: Int) {
    val style = when (headingFactor) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        3 -> MaterialTheme.typography.headlineSmall
        4 -> MaterialTheme.typography.titleLarge
        5 -> MaterialTheme.typography.titleMedium
        6 -> MaterialTheme.typography.titleSmall
        else -> return
    }
    Text(
        text = heading.ownText(),
        style = style,
    )
}

@Composable
fun RenderHtmlPTag(p: Element) {
    Box(Modifier.padding(vertical = 16.dp)) {
        val nodes = p.childNodes() // тут надо что то придумать

        Text(p.wholeText())
    }
}

@Composable
fun RenderHtmlHeadTag(head: Element) {
    Column {
        for (child in head.children()) {
            RenderBlock(child)
        }
    }
}
