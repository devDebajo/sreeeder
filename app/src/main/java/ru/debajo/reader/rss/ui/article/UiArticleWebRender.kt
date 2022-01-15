package ru.debajo.reader.rss.ui.article

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageToken
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenDecoration
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenStyle
import ru.debajo.reader.rss.ui.ext.pxToDp
import ru.debajo.reader.rss.ui.ext.toFinite
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiArticleWebRender(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiArticle: UiArticle
) {
    val viewModel: UiArticleWebRenderViewModel = diViewModel()
    LaunchedEffect(key1 = uiArticle, block = { viewModel.load(uiArticle) })
    val backgroundColor = MaterialTheme.colorScheme.background
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        topBar = {
            Box {
                var toolbarHeight by remember { mutableStateOf(0) }
                Box(
                    Modifier
                        .height(toolbarHeight.pxToDp())
                        .fillMaxWidth(
                            (scrollState.value.toFloat() / scrollState.maxValue.toFloat())
                                .toFinite()
                                .coerceIn(0f, 1f)
                        )
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                )
                CenterAlignedTopAppBar(
                    modifier = Modifier.onGloballyPositioned { toolbarHeight = it.size.height },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleBookmarked(uiArticle) }) {
                            val state by viewModel.state.collectAsState()
                            Icon(
                                imageVector = if (state.bookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { NavGraph.ShareText.navigate(navController, uiArticle.url) }) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { NavGraph.ChromeTabs.navigate(navController, uiArticle.url.toChromeTabsParams(backgroundColor)) }) {
                            Icon(
                                imageVector = Icons.Rounded.OpenInBrowser,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    ) {
        val state by viewModel.state.collectAsState()
        state.let {
            when (it) {
                is UiArticleWebRenderState.Error -> {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.article_web_render_load_failed)
                        )
                    }
                }
                is UiArticleWebRenderState.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                is UiArticleWebRenderState.Prepared -> {
                    SelectionContainer {
                        WebPageTokens(scrollState, it.tokens, uiArticle.title, navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun WebPageTokens(state: ScrollState, tokens: List<WebPageToken>, title: String, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(state),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(20.dp))
        for (token in tokens) {
            when (token) {
                is WebPageToken.Image -> {
                    Image(
                        painter = rememberImagePainter(token.url, builder = { size(OriginalSize) }),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
                is WebPageToken.Text -> {
                    if (token.decoration == null) {
                        TextToken(token, navController)
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            var lineHeight by remember { mutableStateOf(0f) }
                            var textHeight by remember { mutableStateOf(0) }
                            when (token.decoration) {
                                is WebPageTokenDecoration.Bullet -> {
                                    Box(
                                        Modifier
                                            .offset(y = (lineHeight.pxToDp() - 7.dp) / 2f)
                                            .size(7.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(LocalContentColor.current)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                is WebPageTokenDecoration.Quote -> {
                                    Box(
                                        Modifier
                                            .width(2.dp)
                                            .height(textHeight.pxToDp())
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                            TextToken(token, navController) {
                                lineHeight = it.getLineTop(1)
                                textHeight = it.size.height
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextToken(token: WebPageToken.Text, navController: NavController, onTextLayout: (TextLayoutResult) -> Unit = {}) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val text = remember(token, primaryColor) { buildStyledText(token, primaryColor) }
    val onClick: (Int) -> Unit = remember(text) {
        { index: Int ->
            text.getStringAnnotations("URL", index, index)
                .firstOrNull()?.let { stringAnnotation ->
                    NavGraph.ChromeTabs.navigate(navController, stringAnnotation.item.toChromeTabsParams())
                }
        }
    }
    ClickableText(
        text = text,
        onTextLayout = { onTextLayout(it) },
        style = TextStyle.Default.copy(LocalContentColor.current, fontSize = 16.sp),
        onClick = onClick
    )
}

private fun buildStyledText(token: WebPageToken.Text, urlColor: Color): AnnotatedString {
    return buildAnnotatedString {
        append(token.text)
        for (style in token.styles) {
            val styleStart = style.start - token.start
            val styleEnd = style.end - token.start
            when (style) {
                is WebPageTokenStyle.Url -> {
                    addStyle(
                        style = SpanStyle(color = urlColor),
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
                is WebPageTokenStyle.Scale -> {
                    addStyle(SpanStyle(fontSize = 16.sp * style.scale), styleStart, styleEnd)
                }
            }
        }
    }
}
