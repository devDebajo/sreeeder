package ru.debajo.reader.rss.ui.article

import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageToken
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenDecoration
import ru.debajo.reader.rss.ui.article.parser.WebPageTokenStyle
import ru.debajo.reader.rss.ui.common.AppImage
import ru.debajo.reader.rss.ui.common.FontFamilies
import ru.debajo.reader.rss.ui.common.rememberMutableState
import ru.debajo.reader.rss.ui.ext.pxToDp
import ru.debajo.reader.rss.ui.ext.toFinite
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiArticleWebRender(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiArticle: UiArticle
) {
    val viewModel: UiArticleWebRenderViewModel = diViewModel()
    val scrollState = rememberScrollState()
    LaunchedEffect(key1 = uiArticle, block = {
        viewModel.load(uiArticle)
        snapshotFlow { scrollState.maxValue }
            .filter { it < Int.MAX_VALUE }
            .combine(viewModel.scrollPosition) { maxScroll, relativeScroll -> (relativeScroll * maxScroll).roundToInt() }
            .take(1)
            .collect { scroll -> scrollState.animateScrollTo(scroll) }
    })
    val backgroundColor = MaterialTheme.colorScheme.background
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(key1 = uiArticle, effect = {
        onDispose {
            viewModel.saveScroll(uiArticle.id, scrollState.value, scrollState.maxValue)
        }
    })

    Scaffold(
        modifier = modifier,
        topBar = {
            Box {
                var toolbarHeight by rememberMutableState(0)
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
                        WebPageTokens(scrollState, it.tokens, uiArticle.title, navController, coroutineScope)
                    }
                }
            }
        }
    }
}

@Composable
private fun WebPageTokens(
    state: ScrollState,
    tokens: List<WebPageToken>,
    title: String,
    navController: NavController,
    coroutineScope: CoroutineScope,
) {
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
                    var scale by remember { mutableStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }
                    var zIndex by remember { mutableStateOf(0f) }
                    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
                        zIndex = 100f
                        scale *= zoomChange
                        scale = max(scale, 1f)
                        offset += offsetChange
                    }
                    AppImage(
                        url = token.url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(state = transformableState)
                            .pointerInput(Unit) {
                                forEachGesture {
                                    awaitPointerEventScope {
                                        awaitFirstDown(requireUnconsumed = false)
                                        do {
                                            val event = awaitPointerEvent()
                                            val canceled = event.changes.any { it.consumed.positionChange }
                                        } while (!canceled && event.changes.any { it.pressed })

                                        animateFromTo(
                                            coroutineScope = coroutineScope,
                                            typeConverter = Float.VectorConverter,
                                            start = scale,
                                            end = 1f,
                                            onUpdate = { scale = it }
                                        )

                                        animateFromTo(
                                            coroutineScope = coroutineScope,
                                            typeConverter = Offset.VectorConverter,
                                            start = offset,
                                            end = Offset.Zero,
                                            onUpdate = { offset = it }
                                        )

                                        coroutineScope.launch {
                                            delay(500)
                                            zIndex = 0f
                                        }
                                    }
                                }
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .zIndex(zIndex),
                    )
                }
                is WebPageToken.Text -> {
                    if (token.decoration == null) {
                        TextToken(token, navController)
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            var lineHeight by rememberMutableState(0f)
                            var textHeight by rememberMutableState(0)
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
        style = TextStyle.Default.copy(
            color = LocalContentColor.current,
            fontSize = 16.sp,
            fontFamily = FontFamilies.robotoSerif,
            lineHeight = 22.sp
        ),
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

private fun <T, V : AnimationVector> animateFromTo(
    coroutineScope: CoroutineScope,
    typeConverter: TwoWayConverter<T, V>,
    start: T,
    end: T,
    onUpdate: (T) -> Unit,
) {
    val targetBasedAnimation = TargetBasedAnimation(
        animationSpec = tween(300),
        typeConverter = typeConverter,
        initialValue = start,
        targetValue = end
    )

    var playTime: Long
    coroutineScope.launch {
        val startTime = withFrameNanos { it }

        do {
            playTime = withFrameNanos { it } - startTime
            onUpdate(targetBasedAnimation.getValueFromNanos(playTime))
        } while (!targetBasedAnimation.isFinishedFromNanos(playTime))
    }
}
