@file:OptIn(ExperimentalComposeUiApi::class)

package ru.debajo.reader.rss.ui.article

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
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
import ru.debajo.reader.rss.ui.common.IndeterminateProgressIndicator
import ru.debajo.reader.rss.ui.common.rememberMutableState
import ru.debajo.reader.rss.ui.ext.animateTo
import ru.debajo.reader.rss.ui.ext.pxToDp
import ru.debajo.reader.rss.ui.ext.toFinite
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import kotlin.math.PI
import kotlin.math.abs
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
    ) { padding ->
        val state by viewModel.state.collectAsState()
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = state
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (it) {
                    is UiArticleWebRenderState.Error -> {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.article_web_render_load_failed)
                        )
                    }
                    is UiArticleWebRenderState.Loading -> {
                        IndeterminateProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
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
                    val scale = remember { mutableStateOf(1f) }
                    val offset = remember { mutableStateOf(Offset.Zero) }
                    var zIndex by remember { mutableStateOf(0f) }
                    AppImage(
                        url = token.url,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTransformGestures(
                                    panZoomLock = true,
                                    onGesture = { centroid, pan, gestureZoom, _ ->
                                        zIndex = 100f
                                        val oldScale = scale.value
                                        val newScale = scale.value * gestureZoom
                                        offset.value = (offset.value + centroid / oldScale) - (centroid / newScale + pan / oldScale)
                                        scale.value = newScale
                                    }
                                )
                            }
                            .graphicsLayer {
                                translationX = -offset.value.x * scale.value
                                translationY = -offset.value.y * scale.value
                                scaleX = scale.value
                                scaleY = scale.value
                                transformOrigin = TransformOrigin(0f, 0f)
                            }
                            .pointerInput(Unit) {
                                forEachGesture {
                                    awaitPointerEventScope {
                                        awaitFirstDown(requireUnconsumed = false)
                                        do {
                                            val event = awaitPointerEvent()
                                            val canceled = event.changes.any { it.consumed.positionChange }
                                        } while (!canceled && event.changes.any { it.pressed })

                                        coroutineScope.launch {
                                            scale.animateTo(1f)
                                            zIndex = 0f
                                        }
                                        coroutineScope.launch {
                                            offset.animateTo(Offset.Zero)
                                            zIndex = 0f
                                        }
                                    }
                                }
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .zIndex(zIndex)
                            .fillMaxWidth(),
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

private suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            var rotation = 0f
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var lockedToPanZoom = false

            awaitTwoDowns(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()
                val canceled = event.changes.any { it.isConsumed }
                if (!canceled) {
                    val zoomChange = event.calculateZoom()
                    val rotationChange = event.calculateRotation()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        rotation += rotationChange
                        pan += panChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                        val panMotion = pan.getDistance()

                        if (zoomMotion > touchSlop ||
                            rotationMotion > touchSlop ||
                            panMotion > touchSlop
                        ) {
                            pastTouchSlop = true
                            lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                        }
                    }

                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                        if (effectiveRotation != 0f ||
                            zoomChange != 1f ||
                            panChange != Offset.Zero
                        ) {
                            onGesture(centroid, panChange, zoomChange, effectiveRotation)
                        }
                        event.changes.forEach {
                            if (it.positionChanged()) {
                                it.consume()
                            }
                        }
                    }
                }
            } while (!canceled && event.changes.any { it.pressed })
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitTwoDowns(requireUnconsumed: Boolean = true) {
    var event: PointerEvent
    var firstDown: PointerId? = null
    do {
        event = awaitPointerEvent()
        var downPointers = if (firstDown != null) 1 else 0
        event.changes.forEach {
            val isDown =
                if (requireUnconsumed) it.changedToDown() else it.changedToDownIgnoreConsumed()
            val isUp =
                if (requireUnconsumed) it.changedToUp() else it.changedToUpIgnoreConsumed()
            if (isUp && firstDown == it.id) {
                firstDown = null
                downPointers -= 1
            }
            if (isDown) {
                firstDown = it.id
                downPointers += 1
            }
        }
        val satisfied = downPointers > 1
    } while (!satisfied)
}
