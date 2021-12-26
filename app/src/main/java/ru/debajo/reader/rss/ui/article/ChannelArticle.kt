package ru.debajo.reader.rss.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import org.joda.time.DateTime
import org.joda.time.LocalDate
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.common.AppCard
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun ChannelArticle(
    article: UiArticle,
    onFavoriteClick: (UiArticle) -> Unit,
    onClick: (UiArticle) -> Unit,
) {
    AppCard(
        onClick = { onClick(article) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (article.image != null) {
                Image(
                    painter = rememberImagePainter(article.image, builder = {
                        size(OriginalSize)
                    }),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            RoundedCornerShape(18.dp)
                        )
                        .clip(RoundedCornerShape(18.dp)),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(article.title, Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            article.timestamp?.let {
                Text(it.format(), Modifier.padding(horizontal = 16.dp), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (article.channelName != null) {
                ChannelBar(article.channelName, article.channelImage)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(Modifier.weight(1f)) {
                    article.categories.firstOrNull()?.let { CategoryText(it) }
                }
                Icon(
                    modifier = Modifier.clickable { onFavoriteClick(article) },
                    imageVector = if (article.bookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = null
                )
            }
        }

        if (article.isNew) {
            NewIndicator()
        }
    }
}

@Composable
private fun BoxScope.NewIndicator() {
    val squareSize = 40.dp
    val triangleWidth = sqrt(squareSize.value * squareSize.value * 2)
    val triangleHeight = triangleWidth / 2f

    val k = sqrt((triangleHeight / 2f).pow(2f) / 2f)
    val offsetX = triangleWidth - k - triangleWidth / 2f
    val offsetY = k - triangleHeight / 2f

    Box(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .rotate(45f)
            .size(width = triangleWidth.dp, height = triangleHeight.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .align(Alignment.TopEnd)
    ) {
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "New",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
private fun CategoryText(text: String) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            maxLines = 1,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ChannelBar(name: String, image: String?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (image != null) {
            Image(
                painter = rememberImagePainter(image),
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(name, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun DateTime.format(): String {
    val now = LocalDate.now()

    return when {
        toLocalDate() == now -> stringResource(R.string.article_date_time_today, toString("HH:mm"))

        toLocalDate() == now.minusDays(1) -> stringResource(R.string.article_date_time_yesterday, toString("HH:mm"))

        else -> toString("dd MMMM yyyy HH:mm")
    }
}