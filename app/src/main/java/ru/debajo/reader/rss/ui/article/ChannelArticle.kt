package ru.debajo.reader.rss.ui.article

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.joda.time.DateTime
import org.joda.time.LocalDate
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.AppImage
import ru.debajo.reader.rss.ui.ext.hapticClickable
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun ChannelArticle(
    article: UiArticle,
    onFavoriteClick: (UiArticle) -> Unit,
    onView: (UiArticle) -> Unit = {},
    onClick: (UiArticle) -> Unit,
) {
    LaunchedEffect(key1 = article.id, block = { onView(article) })
    AppCard(
        onClick = { onClick(article) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (article.image != null) {
                AppImage(
                    url = article.image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .clip(RoundedCornerShape(18.dp)),
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
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f)) {
                    article.categories.firstOrNull()?.let { CategoryText(it) }
                }
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .hapticClickable(HapticFeedbackType.LongPress) { onFavoriteClick(article) }
                        .padding(10.dp),
                    imageVector = if (article.bookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = article.isNew,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            NewIndicator()
        }
    }
}

@Composable
private fun NewIndicator() {
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
            text = text.trim(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
            AppImage(
                url = image,
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(4.dp)),
                appearAnimation = false,
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