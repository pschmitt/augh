package dev.pschmitt.aughhhh

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
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
    private var incomingIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        incomingIntent = if (savedInstanceState == null) intent else null
        setContent {
            AughhhhTheme {
                AughhhhApp(
                    activity = this,
                    window = window,
                    incomingIntent = incomingIntent,
                    onIntentHandled = { incomingIntent = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingIntent = intent
    }
}

/** Public action and extra names for controlling aughhhh from another Android app. */
object AughhhhIntents {
    const val ACTION_PRESENT = "dev.pschmitt.aughhhh.action.PRESENT"
    const val ACTION_NEXT_PAGE = "dev.pschmitt.aughhhh.action.NEXT_PAGE"
    const val ACTION_PREVIOUS_PAGE = "dev.pschmitt.aughhhh.action.PREVIOUS_PAGE"
    const val ACTION_TRIGGER_ACTION = "dev.pschmitt.aughhhh.action.TRIGGER_ACTION"

    const val EXTRA_TEXT = "dev.pschmitt.aughhhh.extra.TEXT"
    const val EXTRA_PAGES = "dev.pschmitt.aughhhh.extra.PAGES"
    const val EXTRA_FONT = "dev.pschmitt.aughhhh.extra.FONT"
    const val EXTRA_FOREGROUND = "dev.pschmitt.aughhhh.extra.FOREGROUND"
    const val EXTRA_BACKGROUND = "dev.pschmitt.aughhhh.extra.BACKGROUND"
    const val EXTRA_ANIMATION = "dev.pschmitt.aughhhh.extra.ANIMATION"
    const val EXTRA_SPEED = "dev.pschmitt.aughhhh.extra.SPEED"
    const val EXTRA_BLINK_INTENSITY = "dev.pschmitt.aughhhh.extra.BLINK_INTENSITY"
    const val EXTRA_TRANSITION = "dev.pschmitt.aughhhh.extra.TRANSITION"
    const val EXTRA_TAP_ACTION = "dev.pschmitt.aughhhh.extra.TAP_ACTION"
    const val EXTRA_KEEP_SCREEN_AWAKE = "dev.pschmitt.aughhhh.extra.KEEP_SCREEN_AWAKE"
}

private enum class AppMode { EDIT, PRESENT }

private enum class AnimationStyle(val label: String, val description: String, val icon: Int) {
    STATIC("Still", "Clean and calm", R.drawable.ic_static),
    SCROLL("Scroll", "Keep it moving", R.drawable.ic_scroll),
    BLINK("Blink", "Hard to ignore", R.drawable.ic_blink),
    BLINK_BACKGROUND("BG blink", "The background joins in", R.drawable.ic_background),
    STROBE("Strobe", "Very cursed · use carefully", R.drawable.ic_strobe),
    INVERT("Invert", "Swap colors in motion", R.drawable.ic_invert),
}

private enum class TransitionStyle(val label: String, val description: String, val icon: Int) {
    NONE("None", "No drama", R.drawable.ic_none),
    FADE("Fade", "A tasteful entrance", R.drawable.ic_fade),
    WIPE("Wipe", "Corporate presentation energy", R.drawable.ic_wipe),
    BLINDS("Blinds", "You paid for PowerPoint", R.drawable.ic_blinds),
    CHECKERBOARD("Checkerboard", "Absolutely cursed", R.drawable.ic_checkerboard),
    SPIN("Spin", "Please stop the spinning", R.drawable.ic_spin),
}

private enum class TapAction(val label: String, val description: String, val icon: Int) {
    OFF("Off", "Taps do nothing", R.drawable.ic_off),
    INVERT("Invert", "Swap text and background", R.drawable.ic_invert),
    FLASH("Flash", "A tiny attention grab", R.drawable.ic_flash),
    SOUND("Sound", "A tiny device-safe beep", R.drawable.ic_sound),
    NEXT_PAGE("Next page", "Advance the deck", R.drawable.ic_next),
}

private enum class FontChoice(val label: String, val family: FontFamily, val icon: Int) {
    SANS("Modern", FontFamily.SansSerif, R.drawable.ic_looks),
    DISPLAY("Display", FontFamily.Cursive, R.drawable.ic_vibes),
    SERIF("Editorial", FontFamily.Serif, R.drawable.ic_pages),
    MONO("Mono", FontFamily.Monospace, R.drawable.ic_motion),
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
    val icon: Int,
) {
    YELL("Yell", "AUGHHHH!", Palette.CREAM, Palette.PEACH, FontChoice.DISPLAY, AnimationStyle.BLINK, R.drawable.ic_flash),
    APPLAUSE("Applause", "👏👏👏", Palette.INK, Palette.LEMON, FontChoice.DISPLAY, AnimationStyle.STATIC, R.drawable.ic_vibes),
    EMERGENCY("Emergency", "PLEASE WAIT", Palette.WHITE, Palette.PEACH, FontChoice.MONO, AnimationStyle.BLINK, R.drawable.ic_strobe),
    CHILL("Chill", "one sec…", Palette.INK, Palette.MINT, FontChoice.SERIF, AnimationStyle.STATIC, R.drawable.ic_none),
}

private data class SignState(
    val pages: List<String> = listOf("aughhhh"),
    val selectedPage: Int = 0,
    val font: FontChoice = FontChoice.SANS,
    val foreground: Palette = Palette.CREAM,
    val background: Palette = Palette.RED,
    val animation: AnimationStyle = AnimationStyle.STATIC,
    val speed: Float = 1f,
    val blinkRateHz: Float = 2f,
    val blinkIntensity: Float = 0.92f,
    val transition: TransitionStyle = TransitionStyle.NONE,
    val tapAction: TapAction = TapAction.OFF,
    val keepScreenAwake: Boolean = true,
    val maxBrightnessWhenPresenting: Boolean = false,
    val loopPages: Boolean = false,
    val highIntensityMode: Boolean = false,
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
        state = normalize(next)
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
            .putString("foreground", state.foreground.name)
            .putString("background", state.background.name)
            .putString("animation", state.animation.name)
            .putFloat("speed", state.speed)
            .putFloat("blinkRateHz", state.blinkRateHz)
            .putFloat("blinkIntensity", state.blinkIntensity)
            .putString("transition", state.transition.name)
            .putString("tapAction", state.tapAction.name)
            .putBoolean("keepScreenAwake", state.keepScreenAwake)
            .putBoolean("maxBrightnessWhenPresenting", state.maxBrightnessWhenPresenting)
            .putBoolean("loopPages", state.loopPages)
            .putBoolean("highIntensityMode", state.highIntensityMode)
            .putString("recentTexts", JSONArray(state.recentTexts).toString())
            .apply()
    }

    private fun load(): SignState =
        SignState(
            pages = loadPages(),
            selectedPage = preferences.getInt("selectedPage", 0),
            font = enumOrDefault("font", FontChoice.SANS),
            foreground = enumOrDefault("foreground", Palette.CREAM),
            background = enumOrDefault("background", Palette.RED),
            animation = enumOrDefault("animation", AnimationStyle.STATIC),
            speed = preferences.getFloat("speed", SignState().speed),
            blinkRateHz = preferences.getFloat("blinkRateHz", SignState().blinkRateHz),
            blinkIntensity = preferences.getFloat("blinkIntensity", SignState().blinkIntensity),
            transition = enumOrDefault("transition", TransitionStyle.NONE),
            tapAction = enumOrDefault("tapAction", TapAction.OFF),
            keepScreenAwake = preferences.getBoolean("keepScreenAwake", SignState().keepScreenAwake),
            maxBrightnessWhenPresenting = preferences.getBoolean(
                "maxBrightnessWhenPresenting",
                SignState().maxBrightnessWhenPresenting,
            ),
            loopPages = preferences.getBoolean("loopPages", SignState().loopPages),
            highIntensityMode = preferences.getBoolean("highIntensityMode", SignState().highIntensityMode),
            recentTexts = loadRecentTexts(),
        ).let { state -> normalize(state.copy(selectedPage = state.selectedPage.coerceIn(state.pages.indices))) }

    private fun normalize(state: SignState): SignState {
        val maxSpeed = if (state.highIntensityMode) 2f else 1f
        val maxBlinkRate = if (state.highIntensityMode) 10f else 4f
        return state.copy(
            pages = state.pages.ifEmpty { listOf("aughhhh") },
            selectedPage = state.selectedPage.coerceIn(state.pages.ifEmpty { listOf("") }.indices),
            speed = state.speed.coerceIn(0.15f, maxSpeed),
            blinkRateHz = state.blinkRateHz.coerceIn(0.5f, maxBlinkRate),
            animation = if (!state.highIntensityMode && state.animation == AnimationStyle.STROBE) {
                AnimationStyle.BLINK
            } else {
                state.animation
            },
        )
    }

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

private inline fun <reified T : Enum<T>> Intent.enumExtra(key: String): T? =
    getStringExtra(key)?.let { value -> enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) } }

private fun pageIndexAfterMove(index: Int, delta: Int, pageCount: Int, loop: Boolean): Int {
    if (pageCount <= 0) return 0
    val target = index + delta
    return if (loop) Math.floorMod(target, pageCount) else target.coerceIn(0, pageCount - 1)
}

private fun pulseDurationMillis(animation: AnimationStyle, speed: Float, blinkRateHz: Float, refreshRateHz: Float): Int {
    val durationMillis =
        when (animation) {
            AnimationStyle.BLINK, AnimationStyle.BLINK_BACKGROUND, AnimationStyle.STROBE -> {
                val effectiveRate = blinkRateHz.coerceAtMost(refreshRateHz / 2f).coerceAtLeast(0.5f)
                500f / effectiveRate
            }
            else -> 220f / speed
        }
    return durationMillis.roundToInt().coerceAtLeast(50)
}

private fun applyPresentationIntent(intent: Intent, store: SignStore) {
    val suppliedPages =
        intent.getStringArrayListExtra(AughhhhIntents.EXTRA_PAGES)?.toList()
            ?: intent.getStringArrayExtra(AughhhhIntents.EXTRA_PAGES)?.toList()
    val suppliedText = intent.getStringExtra(AughhhhIntents.EXTRA_TEXT) ?: intent.getStringExtra(Intent.EXTRA_TEXT)
    store.update { current ->
        current.copy(
            pages = suppliedPages ?: suppliedText?.let(::listOf) ?: current.pages,
            selectedPage = 0,
            font = intent.enumExtra(AughhhhIntents.EXTRA_FONT) ?: current.font,
            foreground = intent.enumExtra(AughhhhIntents.EXTRA_FOREGROUND) ?: current.foreground,
            background = intent.enumExtra(AughhhhIntents.EXTRA_BACKGROUND) ?: current.background,
            animation = intent.enumExtra(AughhhhIntents.EXTRA_ANIMATION) ?: current.animation,
            speed = if (intent.hasExtra(AughhhhIntents.EXTRA_SPEED)) {
                intent.getFloatExtra(AughhhhIntents.EXTRA_SPEED, current.speed).coerceIn(0.15f, 2f)
            } else current.speed,
            blinkIntensity = if (intent.hasExtra(AughhhhIntents.EXTRA_BLINK_INTENSITY)) {
                intent.getFloatExtra(AughhhhIntents.EXTRA_BLINK_INTENSITY, current.blinkIntensity).coerceIn(0.2f, 1f)
            } else current.blinkIntensity,
            transition = intent.enumExtra(AughhhhIntents.EXTRA_TRANSITION) ?: current.transition,
            tapAction = intent.enumExtra(AughhhhIntents.EXTRA_TAP_ACTION) ?: current.tapAction,
            keepScreenAwake = if (intent.hasExtra(AughhhhIntents.EXTRA_KEEP_SCREEN_AWAKE)) {
                intent.getBooleanExtra(AughhhhIntents.EXTRA_KEEP_SCREEN_AWAKE, current.keepScreenAwake)
            } else current.keepScreenAwake,
        )
    }
}

@Composable
private fun AughhhhApp(
    activity: MainActivity,
    window: android.view.Window,
    incomingIntent: Intent?,
    onIntentHandled: () -> Unit,
) {
    val context = LocalContext.current
    val store = remember { SignStore(context.applicationContext) }
    var modeName by rememberSaveable { mutableStateOf(AppMode.EDIT.name) }
    var presentPage by rememberSaveable { mutableIntStateOf(0) }
    var presentationSession by rememberSaveable { mutableIntStateOf(0) }
    var externalActionTick by rememberSaveable { mutableIntStateOf(0) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val mode = AppMode.valueOf(modeName)

    LaunchedEffect(incomingIntent) {
        val command = incomingIntent ?: return@LaunchedEffect
        when (command.action) {
            AughhhhIntents.ACTION_PRESENT -> {
                applyPresentationIntent(command, store)
                presentPage = 0
                presentationSession++
                modeName = AppMode.PRESENT.name
            }
            AughhhhIntents.ACTION_NEXT_PAGE, AughhhhIntents.ACTION_PREVIOUS_PAGE -> {
                val direction = if (command.action == AughhhhIntents.ACTION_NEXT_PAGE) 1 else -1
                val basePage = if (mode == AppMode.PRESENT) presentPage else store.state.selectedPage
                presentPage = pageIndexAfterMove(basePage, direction, store.state.pages.size, store.state.loopPages)
                if (mode != AppMode.PRESENT) presentationSession++
                modeName = AppMode.PRESENT.name
            }
            AughhhhIntents.ACTION_TRIGGER_ACTION -> {
                if (mode != AppMode.PRESENT) {
                    presentPage = store.state.selectedPage
                    presentationSession++
                }
                modeName = AppMode.PRESENT.name
                externalActionTick++
            }
        }
        onIntentHandled()
    }

    DisposableEffect(mode) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (mode == AppMode.PRESENT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (mode == AppMode.PRESENT) activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (mode == AppMode.PRESENT) {
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    BackHandler(enabled = mode == AppMode.PRESENT || showAbout || showSettings) {
        when {
            mode == AppMode.PRESENT -> modeName = AppMode.EDIT.name
            showAbout -> showAbout = false
            showSettings -> showSettings = false
        }
    }
    if (mode == AppMode.PRESENT) {
        PresentScreen(
            state = store.state,
            window = window,
            initialPage = presentPage.coerceIn(store.state.pages.indices),
            presentationSession = presentationSession,
            externalActionTick = externalActionTick,
            onPageChanged = { presentPage = it },
            onExit = { modeName = AppMode.EDIT.name },
        )
    } else if (showAbout) {
        AboutScreen(onBack = { showAbout = false })
    } else if (showSettings) {
        SettingsScreen(
            state = store.state,
            onStateChange = store::update,
            onBack = { showSettings = false },
            onAbout = {
                showSettings = false
                showAbout = true
            },
        )
    } else {
        EditorScreen(
            state = store.state,
            onStateChange = store::update,
            onRememberRecent = store::rememberRecent,
            onPresent = {
                presentPage = 0
                presentationSession++
                modeName = AppMode.PRESENT.name
            },
            onSettings = { showSettings = true },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun EditorScreen(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
    onRememberRecent: (String) -> Unit,
    onPresent: () -> Unit,
    onSettings: () -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val messageFocusRequester = remember { FocusRequester() }
    var transitionReplayKey by remember { mutableIntStateOf(0) }
    var tapActionPreview by remember { mutableStateOf<TapAction?>(null) }
    var tapActionPreviewKey by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.aughhhh_icon),
                            contentDescription = "aughhhh app logo",
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)),
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("aughhhh", fontWeight = FontWeight.Black)
                            Text(
                                "tiny app · big feelings",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Settings",
                        )
                    }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = onPresent,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp).navigationBarsPadding(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_present),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Present", fontWeight = FontWeight.Bold)
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            state = listState,
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            item {
                Column {
                    Spacer(Modifier.height(14.dp))
                    PresetsCard(state = state, onStateChange = onStateChange, onRememberRecent = onRememberRecent)
                }
            }
            stickyHeader {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    SignPreview(
                        state,
                        modifier = Modifier.padding(vertical = 12.dp),
                        replayKey = transitionReplayKey,
                        tapActionPreview = tapActionPreview,
                        tapActionPreviewKey = tapActionPreviewKey,
                        onPageChange = { delta ->
                            onStateChange { current ->
                                current.copy(selectedPage = (current.selectedPage + delta).coerceIn(current.pages.indices))
                            }
                        },
                        onLongPress = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(3)
                                messageFocusRequester.requestFocus()
                            }
                        },
                    )
                }
            }
            item {
                Column {
                    Spacer(Modifier.height(6.dp))
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
                    RecentMessages(state = state, onStateChange = onStateChange)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(messageFocusRequester)
                        .onFocusChanged { focusState ->
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
                    MotionCard(
                        state = state,
                        onStateChange = onStateChange,
                        onTransitionReplay = { transitionReplayKey++ },
                        onTapActionPreview = { action ->
                            tapActionPreview = action
                            tapActionPreviewKey++
                        },
                    )
                    Spacer(Modifier.height(92.dp))
                }
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
            Icon(
                painter = painterResource(R.drawable.ic_pages),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(8.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.aughhhh_icon),
                contentDescription = "aughhhh app logo",
                modifier = Modifier.size(112.dp).clip(RoundedCornerShape(28.dp)),
            )
            Spacer(Modifier.height(16.dp))
            Text("aughhhh", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("tiny app · big feelings", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(
                "A colorful full-screen sign maker for messages that refuse to be subtle.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Version ${BuildConfig.VERSION_NAME} · GPL-3.0-or-later",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            ExternalLinkCard(
                context = context,
                icon = R.drawable.ic_pages,
                title = "GitHub repository",
                subtitle = "View the source code and report issues",
                url = "https://github.com/pschmitt/aughhhh",
            )
            Spacer(Modifier.height(10.dp))
            ExternalLinkCard(
                context = context,
                icon = R.drawable.ic_vibes,
                title = "Sponsor the project",
                subtitle = "Support development on GitHub Sponsors",
                url = "https://github.com/sponsors/pschmitt",
            )
            Spacer(Modifier.height(10.dp))
            ExternalLinkCard(
                context = context,
                icon = R.drawable.ic_static,
                title = "Privacy policy",
                subtitle = "How aughhhh handles your information",
                url = "https://github.com/pschmitt/aughhhh/blob/main/PRIVACY.md",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
    onBack: () -> Unit,
    onAbout: () -> Unit,
) {
    var showHighIntensityWarning by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
        ) {
            SettingCard(title = "Display", subtitle = "Presentation behavior", icon = R.drawable.ic_display) {
                SettingSwitchRow(
                    title = "Keep screen awake",
                    subtitle = "While presenting",
                    checked = state.keepScreenAwake,
                    onCheckedChange = { enabled -> onStateChange { it.copy(keepScreenAwake = enabled) } },
                )
                SettingSwitchRow(
                    title = "Max brightness",
                    subtitle = "Use full brightness while presenting",
                    checked = state.maxBrightnessWhenPresenting,
                    onCheckedChange = { enabled -> onStateChange { it.copy(maxBrightnessWhenPresenting = enabled) } },
                )
            }
            Spacer(Modifier.height(16.dp))
            SettingCard(title = "Presentation screen", subtitle = "Page navigation", icon = R.drawable.ic_present) {
                SettingSwitchRow(
                    title = "Loop pages",
                    subtitle = "Wrap from the last page to the first, and back again",
                    checked = state.loopPages,
                    onCheckedChange = { enabled -> onStateChange { it.copy(loopPages = enabled) } },
                )
                SettingSwitchRow(
                    title = "High-intensity mode",
                    subtitle = "Unlock faster motion and strobe",
                    checked = state.highIntensityMode,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            showHighIntensityWarning = true
                        } else {
                            onStateChange { it.copy(highIntensityMode = false) }
                        }
                    },
                )
            }
            Spacer(Modifier.height(20.dp))
            Text("Application", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "App-wide options and project information.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onAbout),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("About", fontWeight = FontWeight.Bold)
                        Text(
                            "Version and project links",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("›", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    if (showHighIntensityWarning) {
        AlertDialog(
            onDismissRequest = { showHighIntensityWarning = false },
            title = { Text("High-intensity mode") },
            text = {
                Text(
                    "This unlocks animation speeds above 100% and the Strobe effect. Rapid flashing " +
                        "or high-contrast imagery can cause discomfort or trigger seizures, especially " +
                        "for people with photosensitive epilepsy. Use only if you understand the risk.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showHighIntensityWarning = false
                        onStateChange { it.copy(highIntensityMode = true) }
                    }
                ) {
                    Text("Enable anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHighIntensityWarning = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ExternalLinkCard(context: Context, icon: Int, title: String, subtitle: String, url: String) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("↗", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SignPreview(
    state: SignState,
    modifier: Modifier = Modifier,
    replayKey: Int = 0,
    tapActionPreview: TapAction? = null,
    tapActionPreviewKey: Int = 0,
    onPageChange: ((Int) -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inverted by rememberSaveable(state.selectedPage) { mutableStateOf(false) }
    var flashTick by rememberSaveable(state.selectedPage) { mutableIntStateOf(0) }
    var transitionPreviewKey by remember { mutableIntStateOf(0) }
    var flashActive by remember { mutableStateOf(false) }
    val foreground = if (inverted) state.background.color else state.foreground.color
    val background = if (inverted) state.foreground.color else state.background.color

    LaunchedEffect(flashTick) {
        if (flashTick > 0) {
            flashActive = true
            delay(180)
            flashActive = false
        }
    }

    LaunchedEffect(state.transition, replayKey) { transitionPreviewKey++ }

    fun performTapAction(action: TapAction) {
        when (action) {
            TapAction.OFF -> Unit
            TapAction.INVERT -> inverted = !inverted
            TapAction.FLASH -> flashTick++
            TapAction.SOUND -> coroutineScope.launch { playTapSound(context) }
            TapAction.NEXT_PAGE -> onPageChange?.invoke(1)
        }
    }

    LaunchedEffect(tapActionPreviewKey) {
        tapActionPreview?.let(::performTapAction)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = state.tapAction != TapAction.OFF || onLongPress != null,
                onClick = if (state.tapAction != TapAction.OFF) {
                    { performTapAction(state.tapAction) }
                } else {
                    {}
                },
                onLongClick = onLongPress,
            )
            .then(
                if (onPageChange == null) {
                    Modifier
                } else {
                    Modifier.pointerInput(state.selectedPage, state.pages.size) {
                        var dragDistance = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount -> dragDistance += dragAmount },
                            onDragEnd = {
                                if (abs(dragDistance) > 64f) onPageChange(if (dragDistance < 0) 1 else -1)
                            },
                        )
                    }
                }
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(196.dp)) {
            AnimatedContent(
                targetState = transitionPreviewKey,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    fun duration(base: Int) = (base / state.speed).toInt().coerceAtLeast(60)
                    val enter =
                        when (state.transition) {
                            TransitionStyle.NONE -> fadeIn(tween(0))
                            TransitionStyle.FADE -> fadeIn(tween(duration(260)))
                            TransitionStyle.WIPE -> slideInHorizontally(tween(duration(300))) { it } + fadeIn(tween(duration(300)))
                            TransitionStyle.BLINDS -> scaleIn(tween(duration(320)), initialScale = 0.82f) + fadeIn(tween(duration(320)))
                            TransitionStyle.CHECKERBOARD -> scaleIn(tween(duration(360)), initialScale = 1.18f) + fadeIn(tween(duration(360)))
                            TransitionStyle.SPIN -> scaleIn(tween(duration(360)), initialScale = 0.45f) + fadeIn(tween(duration(360)))
                        }
                    val exit =
                        when (state.transition) {
                            TransitionStyle.NONE -> fadeOut(tween(0))
                            TransitionStyle.FADE -> fadeOut(tween(duration(260)))
                            TransitionStyle.WIPE -> slideOutHorizontally(tween(duration(300))) { -it } + fadeOut(tween(duration(300)))
                            TransitionStyle.BLINDS -> fadeOut(tween(duration(320)))
                            TransitionStyle.CHECKERBOARD -> scaleOut(tween(duration(360)), targetScale = 1.18f) + fadeOut(tween(duration(360)))
                            TransitionStyle.SPIN -> scaleOut(tween(duration(360)), targetScale = 0.45f) + fadeOut(tween(duration(360)))
                        }
                    (enter togetherWith exit).using(SizeTransform(clip = false))
                },
                label = "preview-transition",
            ) { previewKey ->
                key(previewKey) {
                    AnimatedSignText(
                        state = state,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)).padding(22.dp),
                        maxLines = 3,
                        preview = true,
                        foreground = foreground,
                        background = background,
                    )
                }
            }
            PageTransitionOverlay(style = state.transition, page = transitionPreviewKey, color = foreground, speed = state.speed)
            Surface(
                modifier = Modifier.align(Alignment.TopStart),
                color = state.foreground.color.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50),
            ) {
                Text("LIVE PREVIEW", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = state.foreground.color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            if (flashActive) {
                Box(modifier = Modifier.fillMaxSize().background(foreground.copy(alpha = 0.82f)))
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
    SettingCard(title = "Quick vibes", subtitle = "Because choosing is hard", icon = R.drawable.ic_vibes) {
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
                    leadingIcon = { OptionIcon(preset.icon) },
                    label = { Text(preset.label) },
                )
            }
        }
    }
}

@Composable
private fun RecentMessages(state: SignState, onStateChange: (((SignState) -> SignState)) -> Unit) {
    if (state.recentTexts.isEmpty()) return
    Column {
        Text("Recent messages", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
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
        Spacer(Modifier.height(12.dp))
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
    pulseOverride: Float? = null,
) {
    val context = LocalContext.current
    val reducedMotion = rememberReducedMotion(context)
    val animation = if (reducedMotion) AnimationStyle.STATIC else state.animation
    val refreshRateHz = LocalView.current.display?.refreshRate?.takeIf { it > 0f } ?: 60f
    val pulse =
        if (pulseOverride != null) {
            pulseOverride
        } else {
            val transition = rememberInfiniteTransition(label = "sign-motion-${if (preview) "preview" else "present"}")
            val animatedPulse by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = pulseDurationMillis(animation, state.speed, state.blinkRateHz, refreshRateHz),
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "sign-pulse",
            )
            animatedPulse
        }
    val blinkAlpha =
        if (animation == AnimationStyle.BLINK || animation == AnimationStyle.STROBE) {
            (1f - (pulse * state.blinkIntensity * 0.94f)).coerceIn(0.04f, 1f)
        } else 1f
    val animatedBackground =
        when (animation) {
            AnimationStyle.BLINK_BACKGROUND, AnimationStyle.STROBE -> androidx.compose.ui.graphics.lerp(background, foreground, pulse)
            AnimationStyle.INVERT -> androidx.compose.ui.graphics.lerp(background, foreground, pulse)
            else -> background
        }
    val animatedForeground =
        if (animation == AnimationStyle.INVERT) {
            androidx.compose.ui.graphics.lerp(foreground, background, pulse)
        } else foreground
    CompositionLocalProvider(LocalContentColor provides animatedForeground) {
        Box(modifier = modifier.background(animatedBackground), contentAlignment = Alignment.Center) {
            FittedSignText(
                text = state.text.ifBlank { "aughhhh" },
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(blinkAlpha)
                    .then(
                        if (animation == AnimationStyle.SCROLL) {
                            Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = (110f * state.speed).dp,
                            )
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
        val fontSize = fitFontSize(
            textMeasurer = textMeasurer,
            text = text,
            state = state,
            maxWidth = if (maxLines == 1) maxWidth.coerceAtLeast(4096) else maxWidth,
            maxHeight = maxHeight,
            maxLines = maxLines,
        )
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            color = LocalContentColor.current,
            fontFamily = state.font.family,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = (fontSize * 1.05f).sp,
            maxLines = maxLines,
            softWrap = false,
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
    var high = 256f
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
                softWrap = false,
            )
        val fits = !result.didOverflowHeight && !result.didOverflowWidth
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
    SettingCard(title = "Looks", subtitle = "Type is a personality", icon = R.drawable.ic_looks) {
        Text("Font", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        ChipRow {
            FontChoice.entries.forEach { choice ->
                FilterChip(
                    selected = state.font == choice,
                    onClick = { onStateChange { it.copy(font = choice) } },
                    leadingIcon = { OptionIcon(choice.icon) },
                    label = { Text(choice.label) },
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
private fun MotionCard(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
    onTransitionReplay: () -> Unit,
    onTapActionPreview: (TapAction) -> Unit,
) {
    SettingCard(title = "Motion", subtitle = "Optional chaos, responsibly applied", icon = R.drawable.ic_motion) {
        ChipRow {
            AnimationStyle.entries.forEach { animation ->
                FilterChip(
                    selected = state.animation == animation,
                    enabled = state.highIntensityMode || animation != AnimationStyle.STROBE,
                    onClick = { onStateChange { it.copy(animation = animation) } },
                    leadingIcon = { OptionIcon(animation.icon) },
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
        if (!state.highIntensityMode) {
            Text(
                "High-intensity mode unlocks speeds above 100% and Strobe.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (state.animation != AnimationStyle.STATIC) {
            Spacer(Modifier.height(12.dp))
            Text("Speed · ${(state.speed * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = state.speed,
                onValueChange = { onStateChange { current -> current.copy(speed = it) } },
                valueRange = 0.15f..if (state.highIntensityMode) 2f else 1f,
            )
        }
        if (state.animation == AnimationStyle.BLINK ||
            state.animation == AnimationStyle.BLINK_BACKGROUND ||
            state.animation == AnimationStyle.STROBE
        ) {
            Text("Blink rate · ${"%.1f".format(state.blinkRateHz)} Hz", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = state.blinkRateHz,
                onValueChange = { onStateChange { current -> current.copy(blinkRateHz = it) } },
                valueRange = 0.5f..if (state.highIntensityMode) 10f else 4f,
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
                    onClick = {
                        onTransitionReplay()
                        onStateChange { it.copy(transition = transition) }
                    },
                    leadingIcon = { OptionIcon(transition.icon) },
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
                    onClick = {
                        onTapActionPreview(action)
                        onStateChange { it.copy(tapAction = action) }
                    },
                    leadingIcon = { OptionIcon(action.icon) },
                    label = { Text(action.label) },
                )
            }
        }
        Text(
            TapAction.entries.first { it == state.tapAction }.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, icon: Int, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
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
private fun OptionIcon(resourceId: Int) {
    Icon(
        painter = painterResource(resourceId),
        contentDescription = null,
        tint = LocalContentColor.current,
        modifier = Modifier.size(18.dp),
    )
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
                if (selected == palette) {
                    Text(
                        text = "✓",
                        color = if (palette == Palette.INK) Color.White else Color(0xFF24172D),
                        fontSize = 17.sp,
                    )
                }
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
    presentationSession: Int,
    externalActionTick: Int,
    onPageChanged: (Int) -> Unit,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reducedMotion = rememberReducedMotion(context)
    val transitionStyle = if (reducedMotion) TransitionStyle.NONE else state.transition
    val spinRotation = remember { Animatable(0f) }
    var inverted by rememberSaveable(presentationSession) { mutableStateOf(false) }
    var flashTick by rememberSaveable(presentationSession) { mutableIntStateOf(0) }
    var flashActive by remember { mutableStateOf(false) }
    val foreground = if (inverted) state.background.color else state.foreground.color
    val background = if (inverted) state.foreground.color else state.background.color
    val presentPage = initialPage.coerceIn(state.pages.indices)
    val animation = if (reducedMotion) AnimationStyle.STATIC else state.animation
    val refreshRateHz = LocalView.current.display?.refreshRate?.takeIf { it > 0f } ?: 60f
    val pulseTransition = rememberInfiniteTransition(label = "present-background-motion")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = pulseDurationMillis(animation, state.speed, state.blinkRateHz, refreshRateHz),
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "present-background-pulse",
    )
    val animatedPresentationBackground =
        when (animation) {
            AnimationStyle.BLINK_BACKGROUND, AnimationStyle.STROBE, AnimationStyle.INVERT ->
                androidx.compose.ui.graphics.lerp(background, foreground, pulse)
            else -> background
        }

    DisposableEffect(state.keepScreenAwake) {
        if (state.keepScreenAwake) window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    DisposableEffect(window) {
        val previousBrightness = window.attributes.screenBrightness
        if (state.maxBrightnessWhenPresenting) {
            window.attributes = window.attributes.apply { screenBrightness = 1f }
        }
        onDispose {
            window.attributes = window.attributes.apply { screenBrightness = previousBrightness }
        }
    }
    LaunchedEffect(flashTick) {
        if (flashTick > 0) {
            flashActive = true
            delay(180)
            flashActive = false
        }
    }
    LaunchedEffect(presentPage, transitionStyle, state.speed) {
        if (transitionStyle == TransitionStyle.SPIN) {
            spinRotation.snapTo(360f)
            spinRotation.animateTo(0f, tween((360 / state.speed).toInt().coerceAtLeast(70)))
        } else {
            spinRotation.snapTo(0f)
        }
    }

    fun movePage(delta: Int) {
        val next = pageIndexAfterMove(presentPage, delta, state.pages.size, state.loopPages)
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

    LaunchedEffect(externalActionTick) {
        if (externalActionTick > 0) performTapAction()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedPresentationBackground)
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
                fun duration(base: Int) = (base / state.speed).toInt().coerceAtLeast(60)
                val enter =
                    when (transitionStyle) {
                        TransitionStyle.NONE -> fadeIn(tween(0))
                        TransitionStyle.FADE -> fadeIn(tween(duration(260)))
                        TransitionStyle.WIPE -> slideInHorizontally(tween(duration(300))) { it * direction } + fadeIn(tween(duration(300)))
                        TransitionStyle.BLINDS -> scaleIn(tween(duration(320)), initialScale = 0.82f) + fadeIn(tween(duration(320)))
                        TransitionStyle.CHECKERBOARD -> scaleIn(tween(duration(360)), initialScale = 1.18f) + fadeIn(tween(duration(360)))
                        TransitionStyle.SPIN -> scaleIn(tween(duration(360)), initialScale = 0.45f) + fadeIn(tween(duration(360)))
                    }
                val exit =
                    when (transitionStyle) {
                        TransitionStyle.NONE -> fadeOut(tween(0))
                        TransitionStyle.FADE -> fadeOut(tween(duration(260)))
                        TransitionStyle.WIPE -> slideOutHorizontally(tween(duration(300))) { -it * direction } + fadeOut(tween(duration(300)))
                        TransitionStyle.BLINDS -> fadeOut(tween(duration(320)))
                        TransitionStyle.CHECKERBOARD -> scaleOut(tween(duration(360)), targetScale = 1.18f) + fadeOut(tween(duration(360)))
                        TransitionStyle.SPIN -> scaleOut(tween(duration(360)), targetScale = 0.45f) + fadeOut(tween(duration(360)))
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
                pulseOverride = pulse,
            )
        }
        PageTransitionOverlay(style = transitionStyle, page = presentPage, color = foreground, speed = state.speed)
        if (flashActive) {
            Box(modifier = Modifier.fillMaxSize().background(foreground.copy(alpha = 0.82f)))
        }
        TextButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(64.dp)
                .semantics { contentDescription = "Exit present" },
        ) {
            Text("×", color = foreground.copy(alpha = 0.62f), fontSize = 40.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun PageTransitionOverlay(style: TransitionStyle, page: Int, color: Color, speed: Float) {
    if (style != TransitionStyle.BLINDS && style != TransitionStyle.CHECKERBOARD) return
    val progress = remember { Animatable(0f) }
    LaunchedEffect(style, page, speed) {
        progress.snapTo(1f)
        val baseDuration = if (style == TransitionStyle.BLINDS) 320 else 360
        progress.animateTo(0f, tween((baseDuration / speed).toInt().coerceAtLeast(60)))
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
