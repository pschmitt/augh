package dev.pschmitt.aughhhh

import android.content.Context
import android.app.Activity
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AughhhhTheme { AughhhhApp(window = window) } }
    }
}

private enum class AppMode { EDIT, PRESENT }

private enum class AnimationStyle(val label: String, val description: String) {
    STATIC("Still", "Clean and calm"),
    SCROLL("Scroll", "Keep it moving"),
    BLINK("Blink", "Hard to ignore"),
    BLINK_BACKGROUND("BG blink", "The background joins in"),
    STROBE("Strobe", "Very cursed · use carefully"),
}

private enum class TransitionStyle(val label: String, val description: String) {
    NONE("None", "No drama"),
    FADE("Fade", "A tasteful entrance"),
    WIPE("Wipe", "Corporate presentation energy"),
    BLINDS("Blinds", "You paid for PowerPoint"),
    CHECKERBOARD("Checkerboard", "Absolutely cursed"),
    SPIN("Spin", "Please stop the spinning"),
}

private enum class TapAction(val label: String, val description: String) {
    OFF("Off", "Taps do nothing"),
    INVERT("Invert", "Swap text and background"),
    FLASH("Flash", "A tiny attention grab"),
    SOUND("Sound", "A tiny device-safe beep"),
    NEXT_PAGE("Next page", "Advance the deck"),
}

private enum class TextAlignmentChoice(val label: String, val value: TextAlign) {
    LEFT("Left", TextAlign.Left),
    CENTER("Center", TextAlign.Center),
    RIGHT("Right", TextAlign.Right),
}

private enum class VerticalPosition(val label: String, val alignment: Alignment) {
    TOP("Top", Alignment.TopCenter),
    CENTER("Center", Alignment.Center),
    BOTTOM("Bottom", Alignment.BottomCenter),
}

private enum class FontChoice(val label: String, val family: FontFamily) {
    SANS("Modern", FontFamily.SansSerif),
    DISPLAY("Display", FontFamily.Cursive),
    SERIF("Editorial", FontFamily.Serif),
    MONO("Mono", FontFamily.Monospace),
}

private enum class Palette(val label: String, val color: Color) {
    INK("Ink", Color(0xFF17131D)),
    CREAM("Cream", Color(0xFFFFF7E9)),
    RED("Red", Color(0xFFE63946)),
    PEACH("Peach", Color(0xFFFFB38A)),
    PLUM("Plum", Color(0xFF6C2E70)),
    LILAC("Lilac", Color(0xFFC9B6FF)),
    MINT("Mint", Color(0xFFA8E6CF)),
    SKY("Sky", Color(0xFF9ED9FF)),
    LEMON("Lemon", Color(0xFFFFE27A)),
    WHITE("White", Color.White),
}

private enum class Preset(
    val label: String,
    val text: String,
    val foreground: Palette,
    val background: Palette,
    val font: FontChoice,
    val animation: AnimationStyle,
) {
    YELL("Yell", "AUGHHHH!", Palette.CREAM, Palette.PEACH, FontChoice.DISPLAY, AnimationStyle.BLINK),
    APPLAUSE("Applause", "👏👏👏", Palette.INK, Palette.LEMON, FontChoice.DISPLAY, AnimationStyle.STATIC),
    EMERGENCY("Emergency", "PLEASE WAIT", Palette.WHITE, Palette.PEACH, FontChoice.MONO, AnimationStyle.BLINK),
    CHILL("Chill", "one sec…", Palette.INK, Palette.MINT, FontChoice.SERIF, AnimationStyle.STATIC),
}

private data class SignState(
    val pages: List<String> = listOf("aughhhh"),
    val selectedPage: Int = 0,
    val font: FontChoice = FontChoice.SANS,
    val size: Float = 256f,
    val autoSize: Boolean = true,
    val foreground: Palette = Palette.CREAM,
    val background: Palette = Palette.RED,
    val animation: AnimationStyle = AnimationStyle.STATIC,
    val speed: Float = 0.68f,
    val blinkIntensity: Float = 0.92f,
    val transition: TransitionStyle = TransitionStyle.NONE,
    val tapAction: TapAction = TapAction.OFF,
    val textAlignment: TextAlignmentChoice = TextAlignmentChoice.CENTER,
    val verticalPosition: VerticalPosition = VerticalPosition.CENTER,
    val keepScreenAwake: Boolean = true,
    val recentTexts: List<String> = emptyList(),
) {
    val text: String
        get() = pages.getOrNull(selectedPage)?.ifBlank { "" }.orEmpty()
}

private class SignStore(context: Context) {
    private val preferences = context.getSharedPreferences("aughhhh", Context.MODE_PRIVATE)
    var state by mutableStateOf(load())
        private set

    fun update(reducer: (SignState) -> SignState) {
        val next = reducer(state)
        state = next.copy(
            pages = next.pages.ifEmpty { listOf("aughhhh") },
            selectedPage = next.selectedPage.coerceIn(next.pages.ifEmpty { listOf("") }.indices),
        )
        persist()
    }

    fun rememberRecent(text: String) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return
        val recent = (listOf(cleanText) + state.recentTexts.filterNot { it == cleanText }).take(5)
        if (recent != state.recentTexts) {
            state = state.copy(recentTexts = recent)
            persist()
        }
    }

    private fun persist() {
        preferences.edit()
            .putString("pages", JSONArray(state.pages).toString())
            .putInt("selectedPage", state.selectedPage)
            .putString("font", state.font.name)
            .putFloat("size", state.size)
            .putBoolean("autoSize", state.autoSize)
            .putString("foreground", state.foreground.name)
            .putString("background", state.background.name)
            .putString("animation", state.animation.name)
            .putFloat("speed", state.speed)
            .putFloat("blinkIntensity", state.blinkIntensity)
            .putString("transition", state.transition.name)
            .putString("tapAction", state.tapAction.name)
            .putString("textAlignment", state.textAlignment.name)
            .putString("verticalPosition", state.verticalPosition.name)
            .putBoolean("keepScreenAwake", state.keepScreenAwake)
            .putString("recentTexts", JSONArray(state.recentTexts).toString())
            .apply()
    }

    private fun load(): SignState =
        SignState(
            pages = loadPages(),
            selectedPage = preferences.getInt("selectedPage", 0),
            font = enumOrDefault("font", FontChoice.SANS),
            size = preferences.getFloat("size", SignState().size),
            autoSize = preferences.getBoolean("autoSize", SignState().autoSize),
            foreground = enumOrDefault("foreground", Palette.CREAM),
            background = enumOrDefault("background", Palette.RED),
            animation = enumOrDefault("animation", AnimationStyle.STATIC),
            speed = preferences.getFloat("speed", SignState().speed),
            blinkIntensity = preferences.getFloat("blinkIntensity", SignState().blinkIntensity),
            transition = enumOrDefault("transition", TransitionStyle.NONE),
            tapAction = enumOrDefault("tapAction", TapAction.OFF),
            textAlignment = enumOrDefault("textAlignment", TextAlignmentChoice.CENTER),
            verticalPosition = enumOrDefault("verticalPosition", VerticalPosition.CENTER),
            keepScreenAwake = preferences.getBoolean("keepScreenAwake", SignState().keepScreenAwake),
            recentTexts = loadRecentTexts(),
        ).let { state -> state.copy(selectedPage = state.selectedPage.coerceIn(state.pages.indices)) }

    private fun loadPages(): List<String> {
        val savedPages = preferences.getString("pages", null) ?: return listOf("aughhhh")
        return runCatching {
                JSONArray(savedPages).let { pages ->
                    List(pages.length()) { index -> pages.optString(index) }
                }
            }
            .getOrDefault(listOf("aughhhh"))
            .ifEmpty { listOf("aughhhh") }
    }

    private fun loadRecentTexts(): List<String> {
        val savedTexts = preferences.getString("recentTexts", null) ?: return emptyList()
        return runCatching {
                JSONArray(savedTexts).let { texts ->
                    List(texts.length()) { index -> texts.optString(index) }
                }
            }
            .getOrDefault(emptyList())
            .filter { it.isNotBlank() }
            .take(5)
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(key: String, fallback: T): T =
        preferences.getString(key, null)?.let { value ->
            enumValues<T>().firstOrNull { it.name == value }
        } ?: fallback
}

@Composable
private fun AughhhhApp(window: android.view.Window) {
    val context = LocalContext.current
    val store = remember { SignStore(context.applicationContext) }
    var modeName by rememberSaveable { mutableStateOf(AppMode.EDIT.name) }
    var presentPage by rememberSaveable { mutableIntStateOf(0) }
    val mode = AppMode.valueOf(modeName)

    DisposableEffect(mode) {
        val activity = context as? Activity
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (mode == AppMode.PRESENT) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (mode == AppMode.PRESENT) activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            if (mode == AppMode.PRESENT) {
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    BackHandler(enabled = mode == AppMode.PRESENT) { modeName = AppMode.EDIT.name }
    if (mode == AppMode.PRESENT) {
        PresentScreen(
            state = store.state,
            window = window,
            initialPage = presentPage.coerceIn(store.state.pages.indices),
            onPageChanged = { presentPage = it },
            onExit = { modeName = AppMode.EDIT.name },
        )
    } else {
        EditorScreen(
            state = store.state,
            onStateChange = store::update,
            onRememberRecent = store::rememberRecent,
            onPresent = {
                presentPage = store.state.selectedPage
                modeName = AppMode.PRESENT.name
            },
        )
    }
}

@Composable
private fun EditorScreen(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
    onRememberRecent: (String) -> Unit,
    onPresent: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = onPresent,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp).navigationBarsPadding(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("Present full screen", fontWeight = FontWeight.Bold)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).safeDrawingPadding().verticalScroll(rememberScrollState()),
        ) {
            Header()
            PrimaryTabRow(selectedTabIndex = 0, containerColor = Color.Transparent) {
                Tab(selected = true, onClick = {}, text = { Text("Edit", fontWeight = FontWeight.Bold) })
                Tab(selected = false, onClick = onPresent, text = { Text("Present") })
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Text("Your sign, your rules", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Make it loud, make it weird, make it yours.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(18.dp))
                PresetsCard(state = state, onStateChange = onStateChange, onRememberRecent = onRememberRecent)
                Spacer(Modifier.height(12.dp))
                SignPreview(state)
                Spacer(Modifier.height(18.dp))
                PageStrip(
                    pageCount = state.pages.size,
                    selectedPage = state.selectedPage,
                    onSelect = { index -> onStateChange { it.copy(selectedPage = index) } },
                    onAdd = {
                        onStateChange { current ->
                            current.copy(pages = current.pages + "new page", selectedPage = current.pages.size)
                        }
                    },
                    onMove = { from, to ->
                        onStateChange { current ->
                            if (to !in current.pages.indices) current
                            else {
                                val pages = current.pages.toMutableList().apply { add(to, removeAt(from)) }
                                val selected =
                                    when (current.selectedPage) {
                                        from -> to
                                        to -> from
                                        else -> current.selectedPage
                                    }
                                current.copy(pages = pages, selectedPage = selected)
                            }
                        }
                    },
                    onDelete = { index ->
                        onStateChange { current ->
                            if (current.pages.size == 1) {
                                current
                            } else {
                                val pages = current.pages.filterIndexed { pageIndex, _ -> pageIndex != index }
                                val selected =
                                    when {
                                        current.selectedPage > index -> current.selectedPage - 1
                                        current.selectedPage == index -> index.coerceAtMost(pages.lastIndex)
                                        else -> current.selectedPage
                                    }
                                current.copy(pages = pages, selectedPage = selected)
                            }
                        }
                    },
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = state.text,
                    onValueChange = { text ->
                        onStateChange { current ->
                            current.copy(
                                pages = current.pages.mapIndexed { index, page ->
                                    if (index == current.selectedPage) text else page
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                        if (!focusState.isFocused) onRememberRecent(state.text)
                    },
                    label = { Text("Message") },
                    placeholder = { Text("Type something worth displaying…") },
                    minLines = 3,
                    maxLines = 6,
                    supportingText = { Text("${state.text.length} characters") },
                    shape = RoundedCornerShape(20.dp),
                )
                Spacer(Modifier.height(12.dp))
                LooksCard(state = state, onStateChange = onStateChange)
                Spacer(Modifier.height(12.dp))
                MotionCard(state = state, onStateChange = onStateChange)
                Spacer(Modifier.height(92.dp))
            }
        }
    }
}

@Composable
private fun PageStrip(
    pageCount: Int,
    selectedPage: Int,
    onSelect: (Int) -> Unit,
    onAdd: () -> Unit,
    onMove: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pages", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text("$pageCount total", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pageCount) { index ->
                Surface(
                    color = if (index == selectedPage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(14.dp),
                    onClick = { onSelect(index) },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Page ${index + 1}",
                            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
                            color = if (index == selectedPage) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        if (pageCount > 1) {
                            TextButton(
                                onClick = { onMove(index, index - 1) },
                                enabled = index > 0,
                            ) {
                                Text("‹", fontSize = 18.sp)
                            }
                            TextButton(
                                onClick = { onMove(index, index + 1) },
                                enabled = index < pageCount - 1,
                            ) {
                                Text("›", fontSize = 18.sp)
                            }
                            TextButton(onClick = { onDelete(index) }) {
                                Text("×", fontSize = 18.sp)
                            }
                        } else {
                            Spacer(Modifier.width(14.dp))
                        }
                    }
                }
            }
            TextButton(onClick = onAdd) { Text("+ Add page") }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(
                Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFFFC857), Color(0xFFB28DFF)))
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text("A!", color = Color(0xFF24172D), fontWeight = FontWeight.Black, fontSize = 19.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("aughhhh", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("tiny app · big feelings", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SignPreview(state: SignState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(196.dp)) {
            AnimatedSignText(
                state = state,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)).padding(22.dp),
                maxLines = 3,
                preview = true,
            )
            Surface(
                modifier = Modifier.align(Alignment.TopStart),
                color = state.foreground.color.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50),
            ) {
                Text("LIVE PREVIEW", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = state.foreground.color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PresetsCard(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
    onRememberRecent: (String) -> Unit,
) {
    SettingCard(title = "Quick vibes", subtitle = "Because choosing is hard") {
        ChipRow {
            Preset.entries.forEach { preset ->
                FilterChip(
                    selected = false,
                    onClick = {
                        onRememberRecent(state.text)
                        onStateChange { current ->
                            current.copy(
                                pages = current.pages.mapIndexed { index, page ->
                                    if (index == current.selectedPage) preset.text else page
                                },
                                font = preset.font,
                                foreground = preset.foreground,
                                background = preset.background,
                                animation = preset.animation,
                            )
                        }
                    },
                    label = { Text(preset.label) },
                )
            }
        }
        if (state.recentTexts.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("Recent", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            ChipRow {
                state.recentTexts.forEach { recent ->
                    FilterChip(
                        selected = recent == state.text,
                        onClick = {
                            onStateChange { current ->
                                current.copy(
                                    pages = current.pages.mapIndexed { index, page ->
                                        if (index == current.selectedPage) recent else page
                                    }
                                )
                            }
                        },
                        label = { Text(recent.take(18)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedSignText(
    state: SignState,
    modifier: Modifier,
    maxLines: Int,
    preview: Boolean,
    foreground: Color = state.foreground.color,
    background: Color = state.background.color,
) {
    val context = LocalContext.current
    val reducedMotion = rememberReducedMotion(context)
    val animation = if (reducedMotion) AnimationStyle.STATIC else state.animation
    val transition = rememberInfiniteTransition(label = "sign-motion-${if (preview) "preview" else "present"}")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (preview) (2400 / state.speed).toInt() else (1450 / state.speed).toInt().coerceAtLeast(120),
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sign-pulse",
    )
    val blinkAlpha =
        if (animation == AnimationStyle.BLINK || animation == AnimationStyle.STROBE) {
            (1f - (pulse * state.blinkIntensity * 0.94f)).coerceIn(0.04f, 1f)
        } else 1f
    val animatedBackground =
        if (animation == AnimationStyle.BLINK_BACKGROUND || animation == AnimationStyle.STROBE) {
            androidx.compose.ui.graphics.lerp(background, foreground, pulse)
        } else background
    CompositionLocalProvider(LocalContentColor provides foreground) {
        Box(modifier = modifier.background(animatedBackground), contentAlignment = state.verticalPosition.alignment) {
            FittedSignText(
                text = state.text.ifBlank { "aughhhh" },
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(blinkAlpha)
                    .then(
                        if (animation == AnimationStyle.SCROLL) {
                            Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        } else Modifier
                    ),
                maxLines = if (animation == AnimationStyle.SCROLL) 1 else maxLines,
            )
        }
    }
}

@Composable
private fun FittedSignText(
    text: String,
    state: SignState,
    modifier: Modifier,
    maxLines: Int,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val maxWidth = with(density) { maxWidth.toPx().roundToInt().coerceAtLeast(1) }
        val maxHeight = with(density) { maxHeight.toPx().roundToInt().coerceAtLeast(1) }
        val fontSize =
            if (state.autoSize) {
                fitFontSize(
                    textMeasurer = textMeasurer,
                    text = text,
                    state = state,
                    maxWidth = if (maxLines == 1) maxWidth.coerceAtLeast(4096) else maxWidth,
                    maxHeight = maxHeight,
                    maxLines = maxLines,
                )
            } else state.size
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            color = LocalContentColor.current,
            fontFamily = state.font.family,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = state.textAlignment.value,
            lineHeight = (fontSize * 1.05f).sp,
            maxLines = maxLines,
        )
    }
}

private fun fitFontSize(
    textMeasurer: TextMeasurer,
    text: String,
    state: SignState,
    maxWidth: Int,
    maxHeight: Int,
    maxLines: Int,
): Float {
    var low = 18f
    var high = state.size.coerceIn(low, 256f)
    repeat(12) {
        val candidate = (low + high) / 2f
        val result =
            textMeasurer.measure(
                text = text,
                style = TextStyle(
                    fontFamily = state.font.family,
                    fontSize = candidate.sp,
                    fontWeight = FontWeight.Bold,
                ),
                constraints = Constraints(maxWidth = maxWidth, maxHeight = maxHeight),
                maxLines = maxLines,
            )
        val fits = !result.didOverflowHeight && (maxLines == 1 || !result.didOverflowWidth)
        if (fits) low = candidate else high = candidate
    }
    return low
}

@Composable
private fun rememberReducedMotion(context: Context): Boolean {
    val resolver = context.contentResolver
    val animatorScale = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    val transitionScale = Settings.Global.getFloat(resolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)
    return animatorScale == 0f || transitionScale == 0f
}

@Composable
private fun LooksCard(state: SignState, onStateChange: (((SignState) -> SignState)) -> Unit) {
    SettingCard(title = "Looks", subtitle = "Type is a personality") {
        Text("Font", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            FontChoice.entries.forEach { choice ->
                FilterChip(
                    selected = state.font == choice,
                    onClick = { onStateChange { it.copy(font = choice) } },
                    label = { Text(choice.label) },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (state.autoSize) "Size · auto-fit" else "Size · ${state.size.toInt()} sp",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text("Max ${state.size.toInt()} sp", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = state.autoSize,
                onCheckedChange = { enabled -> onStateChange { it.copy(autoSize = enabled) } },
            )
        }
        Slider(
            value = state.size,
            onValueChange = { onStateChange { current -> current.copy(size = it, autoSize = false) } },
            valueRange = 48f..256f,
        )
        Text("Text alignment", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            TextAlignmentChoice.entries.forEach { alignment ->
                FilterChip(
                    selected = state.textAlignment == alignment,
                    onClick = { onStateChange { it.copy(textAlignment = alignment) } },
                    label = { Text(alignment.label) },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Vertical position", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            VerticalPosition.entries.forEach { position ->
                FilterChip(
                    selected = state.verticalPosition == position,
                    onClick = { onStateChange { it.copy(verticalPosition = position) } },
                    label = { Text(position.label) },
                )
            }
        }
        ColorPicker(
            title = "Text color",
            selected = state.foreground,
            onSelect = { color -> onStateChange { it.copy(foreground = color) } },
        )
        Spacer(Modifier.height(12.dp))
        ColorPicker(
            title = "Background",
            selected = state.background,
            onSelect = { color -> onStateChange { it.copy(background = color) } },
        )
        if (contrastRatio(state.foreground.color, state.background.color) < 3f) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Low contrast: your sign may be hard to read from a distance.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun contrastRatio(first: Color, second: Color): Float {
    val firstLuminance = relativeLuminance(first)
    val secondLuminance = relativeLuminance(second)
    val brighter = firstLuminance.coerceAtLeast(secondLuminance)
    val darker = firstLuminance.coerceAtMost(secondLuminance)
    return (brighter + 0.05f) / (darker + 0.05f)
}

private fun relativeLuminance(color: Color): Float {
    fun channel(value: Float): Float =
        if (value <= 0.03928f) value / 12.92f else ((value + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    return 0.2126f * channel(color.red) + 0.7152f * channel(color.green) + 0.0722f * channel(color.blue)
}

@Composable
private fun MotionCard(state: SignState, onStateChange: (((SignState) -> SignState)) -> Unit) {
    SettingCard(title = "Motion", subtitle = "Optional chaos, responsibly applied") {
        ChipRow {
            AnimationStyle.entries.forEach { animation ->
                FilterChip(
                    selected = state.animation == animation,
                    onClick = { onStateChange { it.copy(animation = animation) } },
                    label = { Text(animation.label) },
                )
            }
        }
        Text(
            state.animation.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (state.animation == AnimationStyle.STROBE) {
            Text(
                "Strobe can trigger discomfort or photosensitive reactions.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
        if (state.animation != AnimationStyle.STATIC) {
            Spacer(Modifier.height(12.dp))
            Text("Speed · ${(state.speed * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = state.speed,
                onValueChange = { onStateChange { current -> current.copy(speed = it) } },
                valueRange = 0.15f..1f,
            )
        }
        if (state.animation == AnimationStyle.BLINK || state.animation == AnimationStyle.STROBE) {
            Text("Blink intensity · ${(state.blinkIntensity * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = state.blinkIntensity,
                onValueChange = { onStateChange { current -> current.copy(blinkIntensity = it) } },
                valueRange = 0.2f..1f,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text("Page transition", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            TransitionStyle.entries.forEach { transition ->
                FilterChip(
                    selected = state.transition == transition,
                    onClick = { onStateChange { it.copy(transition = transition) } },
                    label = { Text(transition.label) },
                )
            }
        }
        Text(
            TransitionStyle.entries.first { it == state.transition }.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))
        Text("Tap action", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            TapAction.entries.forEach { action ->
                FilterChip(
                    selected = state.tapAction == action,
                    onClick = { onStateChange { it.copy(tapAction = action) } },
                    label = { Text(action.label) },
                )
            }
        }
        Text(
            TapAction.entries.first { it == state.tapAction }.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Keep screen awake", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("While presenting", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = state.keepScreenAwake,
                onCheckedChange = { enabled -> onStateChange { it.copy(keepScreenAwake = enabled) } },
            )
        }
        if (rememberReducedMotion(LocalContext.current)) {
            Text(
                "Reduced motion is enabled; dramatic transitions and strobe are softened.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        content()
    }
}

@Composable
private fun ColorPicker(title: String, selected: Palette, onSelect: (Palette) -> Unit) {
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Palette.entries.forEach { palette ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(palette.color)
                    .border(
                        width = if (selected == palette) 3.dp else 1.dp,
                        color = if (selected == palette) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    )
                    .clickable(onClick = { onSelect(palette) })
                    .semantics {
                        contentDescription = "${title}: ${palette.label}"
                        role = Role.RadioButton
                        this.selected = selected == palette
                    },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = if (selected == palette) "✓" else "",
                    modifier = Modifier.fillMaxSize(),
                    style = TextStyle(color = if (palette == Palette.INK) Color.White else Color(0xFF24172D), fontSize = 17.sp, textAlign = TextAlign.Center),
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PresentScreen(
    state: SignState,
    window: android.view.Window,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reducedMotion = rememberReducedMotion(context)
    val transitionStyle = if (reducedMotion) TransitionStyle.NONE else state.transition
    val spinRotation = remember { Animatable(0f) }
    var inverted by rememberSaveable { mutableStateOf(false) }
    var flashTick by rememberSaveable { mutableIntStateOf(0) }
    var flashActive by remember { mutableStateOf(false) }
    val foreground = if (inverted) state.background.color else state.foreground.color
    val background = if (inverted) state.foreground.color else state.background.color
    val presentPage = initialPage.coerceIn(state.pages.indices)

    DisposableEffect(state.keepScreenAwake) {
        if (state.keepScreenAwake) window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    LaunchedEffect(flashTick) {
        if (flashTick > 0) {
            flashActive = true
            delay(180)
            flashActive = false
        }
    }
    LaunchedEffect(presentPage, transitionStyle) {
        if (transitionStyle == TransitionStyle.SPIN) {
            spinRotation.snapTo(360f)
            spinRotation.animateTo(0f, tween(800))
        } else {
            spinRotation.snapTo(0f)
        }
    }

    fun movePage(delta: Int) {
        val next = (presentPage + delta).coerceIn(state.pages.indices)
        if (next != presentPage) onPageChanged(next)
    }

    fun performTapAction() {
        when (state.tapAction) {
            TapAction.OFF -> Unit
            TapAction.INVERT -> inverted = !inverted
            TapAction.FLASH -> flashTick++
            TapAction.SOUND -> coroutineScope.launch { playTapSound(context) }
            TapAction.NEXT_PAGE -> movePage(1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .safeDrawingPadding()
            .pointerInput(state.pages.size, presentPage) {
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount -> dragDistance += dragAmount },
                    onDragEnd = {
                        if (abs(dragDistance) > 64f) {
                            if (dragDistance < 0) movePage(1) else movePage(-1)
                        }
                    },
                )
            }
            .clickable(onClick = ::performTapAction),
    ) {
        AnimatedContent(
            targetState = presentPage,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).graphicsLayer(rotationZ = spinRotation.value),
            transitionSpec = {
                val direction = if (targetState >= initialState) 1 else -1
                val enter =
                    when (transitionStyle) {
                        TransitionStyle.NONE -> fadeIn(tween(0))
                        TransitionStyle.FADE -> fadeIn(tween(500))
                        TransitionStyle.WIPE -> slideInHorizontally(tween(650)) { it * direction } + fadeIn(tween(650))
                        TransitionStyle.BLINDS -> scaleIn(tween(700), initialScale = 0.82f) + fadeIn(tween(700))
                        TransitionStyle.CHECKERBOARD -> scaleIn(tween(800), initialScale = 1.18f) + fadeIn(tween(800))
                        TransitionStyle.SPIN -> scaleIn(tween(800), initialScale = 0.45f) + fadeIn(tween(800))
                    }
                val exit =
                    when (transitionStyle) {
                        TransitionStyle.NONE -> fadeOut(tween(0))
                        TransitionStyle.FADE -> fadeOut(tween(500))
                        TransitionStyle.WIPE -> slideOutHorizontally(tween(650)) { -it * direction } + fadeOut(tween(650))
                        TransitionStyle.BLINDS -> fadeOut(tween(700))
                        TransitionStyle.CHECKERBOARD -> scaleOut(tween(800), targetScale = 1.18f) + fadeOut(tween(800))
                        TransitionStyle.SPIN -> scaleOut(tween(800), targetScale = 0.45f) + fadeOut(tween(800))
                    }
                (enter togetherWith exit).using(SizeTransform(clip = false))
            },
            label = "page-transition",
        ) { page ->
            AnimatedSignText(
                state = state.copy(pages = listOf(state.pages[page]), selectedPage = 0),
                modifier = Modifier.fillMaxSize(),
                maxLines = 4,
                preview = false,
                foreground = foreground,
                background = background,
            )
        }
        PageTransitionOverlay(style = transitionStyle, page = presentPage, color = foreground)
        if (flashActive) {
            Box(modifier = Modifier.fillMaxSize().background(foreground.copy(alpha = 0.82f)))
        }
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            color = foreground.copy(alpha = 0.12f),
            contentColor = foreground,
            shape = RoundedCornerShape(50),
        ) {
            TextButton(onClick = onExit) { Text("Exit present") }
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${presentPage + 1} / ${state.pages.size}  ·  swipe to navigate",
                color = foreground.copy(alpha = 0.62f),
                style = MaterialTheme.typography.labelMedium,
            )
            if (state.tapAction != TapAction.OFF) {
                Text(
                    text = "tap: ${state.tapAction.label.lowercase()}",
                    color = foreground.copy(alpha = 0.45f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun PageTransitionOverlay(style: TransitionStyle, page: Int, color: Color) {
    if (style != TransitionStyle.BLINDS && style != TransitionStyle.CHECKERBOARD) return
    val progress = remember { Animatable(0f) }
    LaunchedEffect(style, page) {
        progress.snapTo(1f)
        progress.animateTo(0f, tween(if (style == TransitionStyle.BLINDS) 700 else 850))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        when (style) {
            TransitionStyle.BLINDS -> {
                val blindHeight = size.height / 10f
                repeat(10) { index ->
                    if (index % 2 == 0) {
                        drawRect(
                            color = color.copy(alpha = progress.value * 0.94f),
                            topLeft = androidx.compose.ui.geometry.Offset(0f, index * blindHeight),
                            size = androidx.compose.ui.geometry.Size(size.width, blindHeight * progress.value),
                        )
                    }
                }
            }
            TransitionStyle.CHECKERBOARD -> {
                val cell = 56f
                val columns = (size.width / cell).toInt() + 1
                val rows = (size.height / cell).toInt() + 1
                repeat(rows) { row ->
                    repeat(columns) { column ->
                        if ((row + column) % 2 == 0) {
                            drawRect(
                                color = color.copy(alpha = progress.value * 0.9f),
                                topLeft = androidx.compose.ui.geometry.Offset(column * cell, row * cell),
                                size = androidx.compose.ui.geometry.Size(cell, cell),
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun playTapSound(context: Context) {
    val audioManager = context.getSystemService(AudioManager::class.java)
    if (audioManager?.ringerMode == AudioManager.RINGER_MODE_SILENT) return
    val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
    delay(180)
    tone.release()
}

@Composable
private fun AughhhhTheme(content: @Composable () -> Unit) {
    val darkColors = androidx.compose.material3.darkColorScheme(
        primary = Color(0xFFFFB38A),
        secondary = Color(0xFFC9B6FF),
        tertiary = Color(0xFFA8E6CF),
    )
    val lightColors = androidx.compose.material3.lightColorScheme(
        primary = Color(0xFF8F3C00),
        secondary = Color(0xFF675080),
        tertiary = Color(0xFF236B54),
    )
    val context = LocalContext.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val colorScheme =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                if (isDark) androidx.compose.material3.dynamicDarkColorScheme(context)
                else androidx.compose.material3.dynamicLightColorScheme(context)
            isDark -> darkColors
            else -> lightColors
        }
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
