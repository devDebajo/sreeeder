package ru.debajo.reader.rss.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import ru.debajo.reader.rss.ui.article.parser.WebPageToken
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenDecoration
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenStyle
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPage(modifier: Modifier = Modifier, navController: NavController, htmlContent: String) {
    Scaffold(modifier = modifier) {
        val tokens = remember(htmlContent) { WebPageParser.parse(htmlContent) }
        WebPageTokens(tokens, navController)
    }
}

@Composable
private fun WebPageTokens(tokens: List<WebPageToken>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        for (token in tokens) {
            when (token) {
                is WebPageToken.Image -> {
                    Image(
                        painter = rememberImagePainter(token.url, builder = { size(OriginalSize) }),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                    )
                }
                is WebPageToken.Text -> {
                    if (token.decoration == null) {
                        TextToken(token, navController)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            var textHeight by remember { mutableStateOf(0) }
                            when (token.decoration) {
                                is WebPageTokenDecoration.Bullet -> {
                                    Box(
                                        Modifier
                                            .size(7.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(LocalContentColor.current)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                is WebPageTokenDecoration.Quote -> {
                                    val height = with(LocalDensity.current) { textHeight.toDp() }
                                    Box(
                                        Modifier
                                            .width(2.dp)
                                            .height(height)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                            TextToken(token, navController) { textHeight = it }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextToken(token: WebPageToken.Text, navController: NavController, heightProvider: (Int) -> Unit = {}) {
    val text = buildStyledText(token)
    ClickableText(
        text = text,
        onTextLayout = { heightProvider(it.size.height) },
        style = TextStyle.Default.copy(LocalContentColor.current, fontSize = 16.sp),
        onClick = {
            text.getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    NavGraph.ChromeTabs.navigate(navController, stringAnnotation.item.toChromeTabsParams())
                }
        }
    )
}

@Composable
private fun buildStyledText(token: WebPageToken.Text): AnnotatedString {
    return buildAnnotatedString {
        append(token.text)
        for (style in token.styles) {
            val styleStart = style.start - token.start
            val styleEnd = style.end - token.start
            when (style) {
                is WebPageTokenStyle.Url -> {
                    addStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.primary),
                        start = styleStart,
                        end = styleEnd
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = style.url,
                        start = styleStart,
                        end = styleEnd
                    )
                }
                is WebPageTokenStyle.Bold -> {
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), styleStart, styleEnd)
                }
                is WebPageTokenStyle.Italic -> {
                    addStyle(SpanStyle(fontStyle = FontStyle.Italic), styleStart, styleEnd)
                }
                is WebPageTokenStyle.BoldItalic -> {
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), styleStart, styleEnd)
                }
                is WebPageTokenStyle.ForegroundColor -> {
                    addStyle(SpanStyle(color = style.color), styleStart, styleEnd)
                }
                is WebPageTokenStyle.Underline -> {
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), styleStart, styleEnd)
                }
            }
        }
    }
}
