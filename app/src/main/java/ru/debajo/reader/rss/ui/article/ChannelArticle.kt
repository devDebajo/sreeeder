package ru.debajo.reader.rss.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.AppCard

@Composable
fun ChannelArticle(
    article: UiArticle,
    channel: UiChannel? = null
) {
    if (article.image != null) {
        ChannelArticleWithImage(article, channel)
    } else {
        ChannelArticleWithoutImage(article, channel)
    }
}

@Composable
private fun ChannelArticleWithImage(
    article: UiArticle,
    channel: UiChannel?
) {
    AppCard(
        onClick = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Image(
                painter = rememberImagePainter(article.image, builder = {
                    size(OriginalSize)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(article.title, Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}

@Composable
private fun ChannelArticleWithoutImage(
    article: UiArticle,
    channel: UiChannel?
) {
    AppCard(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(article.title)
    }
}
