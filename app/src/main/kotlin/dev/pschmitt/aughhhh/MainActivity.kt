package dev.pschmitt.aughhhh

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.abs
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
    PEACH("Peach", Color(0xFFFFB38A)),
    LILAC("Lilac", Color(0xFFC9B6FF)),
    MINT("Mint", Color(0xFFA8E6CF)),
    SKY("Sky", Color(0xFF9ED9FF)),
    LEMON("Lemon", Color(0xFFFFE27A)),
    WHITE("White", Color.White),
}

private data class SignState(
    val pages: List<String> = listOf("aughhhh"),
    val selectedPage: Int = 0,
    val font: FontChoice = FontChoice.DISPLAY,
    val size: Float = 74f,
    val foreground: Palette = Palette.CREAM,
    val background: Palette = Palette.INK,
    val animation: AnimationStyle = AnimationStyle.STATIC,
    val speed: Float = 0.55f,
) {
    val text: String
        get() = pages.getOrNull(selectedPage)?.ifBlank { "" }.orEmpty()
}

private class SignStore(context: Context) {
    private val preferences = context.getSharedPreferences("aughhhh", Context.MODE_PRIVATE)
    var state by mutableStateOf(load())
        private set

    fun update(reducer: (SignState) -> SignState) {
        state = reducer(state)
        preferences.edit()
            .putString("pages", JSONArray(state.pages).toString())
            .putInt("selectedPage", state.selectedPage)
            .putString("font", state.font.name)
            .putFloat("size", state.size)
            .putString("foreground", state.foreground.name)
            .putString("background", state.background.name)
            .putString("animation", state.animation.name)
            .putFloat("speed", state.speed)
            .apply()
    }

    private fun load(): SignState =
        SignState(
            pages = loadPages(),
            selectedPage = preferences.getInt("selectedPage", 0),
            font = enumOrDefault("font", FontChoice.DISPLAY),
            size = preferences.getFloat("size", SignState().size),
            foreground = enumOrDefault("foreground", Palette.CREAM),
            background = enumOrDefault("background", Palette.INK),
            animation = enumOrDefault("animation", AnimationStyle.STATIC),
            speed = preferences.getFloat("speed", SignState().speed),
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

    private inline fun <reified T : Enum<T>> enumOrDefault(key: String, fallback: T): T =
        preferences.getString(key, null)?.let { value ->
            enumValues<T>().firstOrNull { it.name == value }
        } ?: fallback
}

@Composable
private fun AughhhhApp(window: android.view.Window) {
    val context = LocalContext.current
    val store = remember { SignStore(context.applicationContext) }
    var mode by remember { mutableStateOf(AppMode.EDIT) }

    DisposableEffect(mode) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (mode == AppMode.PRESENT) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (mode == AppMode.PRESENT) {
                WindowCompat.getInsetsController(window, window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    BackHandler(enabled = mode == AppMode.PRESENT) { mode = AppMode.EDIT }
    if (mode == AppMode.PRESENT) {
        PresentScreen(state = store.state, onExit = { mode = AppMode.EDIT })
    } else {
        EditorScreen(
            state = store.state,
            onStateChange = store::update,
            onPresent = { mode = AppMode.PRESENT },
        )
    }
}

@Composable
private fun EditorScreen(
    state: SignState,
    onStateChange: (((SignState) -> SignState)) -> Unit,
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
            TabRow(selectedTabIndex = 0, containerColor = Color.Transparent) {
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
                    modifier = Modifier.fillMaxWidth(),
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
        colors = CardDefaults.cardColors(containerColor = state.background.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(196.dp).padding(22.dp), contentAlignment = Alignment.Center) {
            Text(
                text = state.text.ifBlank { "aughhhh" },
                color = state.foreground.color,
                fontFamily = state.font.family,
                fontSize = (state.size * 0.55f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 3,
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
        Text("Size · ${state.size.toInt()} sp", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Slider(
            value = state.size,
            onValueChange = { onStateChange { state -> state.copy(size = it) } },
            valueRange = 28f..128f,
        )
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
    }
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
        Text(state.animation.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (state.animation != AnimationStyle.STATIC) {
            Spacer(Modifier.height(12.dp))
            Text("Speed · ${(state.speed * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Slider(
                value = state.speed,
                onValueChange = { onStateChange { current -> current.copy(speed = it) } },
                valueRange = 0.15f..1f,
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
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Palette.entries.forEach { palette ->
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape).background(palette.color).border(
                    width = if (selected == palette) 3.dp else 1.dp,
                    color = if (selected == palette) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape,
                ).clickable(onClick = { onSelect(palette) }),
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

@Composable
private fun PresentScreen(state: SignState, onExit: () -> Unit) {
    var presentPage by remember { mutableStateOf(state.selectedPage.coerceIn(state.pages.indices)) }
    val blinkTransition = rememberInfiniteTransition(label = "blink")
    val blinkAlpha by blinkTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween((1400 / state.speed).toInt()), RepeatMode.Reverse),
        label = "blinkAlpha",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(state.background.color)
            .safeDrawingPadding()
            .pointerInput(state.pages.size, presentPage) {
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        dragDistance += dragAmount
                    },
                    onDragEnd = {
                        if (abs(dragDistance) > 64f) {
                            presentPage =
                                if (dragDistance < 0) {
                                    (presentPage + 1).coerceAtMost(state.pages.lastIndex)
                                } else {
                                    (presentPage - 1).coerceAtLeast(0)
                                }
                        }
                    },
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = state.pages.getOrNull(presentPage).orEmpty().ifBlank { "aughhhh" },
                modifier = Modifier.fillMaxWidth().then(if (state.animation == AnimationStyle.SCROLL) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier).alpha(if (state.animation == AnimationStyle.BLINK) blinkAlpha else 1f),
                color = state.foreground.color,
                fontFamily = state.font.family,
                fontSize = state.size.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = (state.size * 1.05f).sp,
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            color = state.foreground.color.copy(alpha = 0.12f),
            contentColor = state.foreground.color,
            shape = RoundedCornerShape(50),
        ) {
            TextButton(onClick = onExit) { Text("Exit present") }
        }
        Text(
            text = "${presentPage + 1} / ${state.pages.size}  ·  ${state.animation.label.lowercase()}",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
            color = state.foreground.color.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
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
