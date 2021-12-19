package ru.debajo.reader.rss.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.debajo.reader.rss.ui.ext.optionalClickable

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .optionalClickable(onClick)
            .then(modifier),
    ) {
        content()
    }
}
