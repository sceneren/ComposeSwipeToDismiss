package com.github.sceneren.compose.swipe.simple

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.sceneren.compose.swipe.simple.theme.ComposeSwipeTheme
import com.github.sceneren.compose.swipe.DragAnchors
import com.github.sceneren.compose.swipe.SwipeRow
import com.github.sceneren.compose.swipe.SwipeRowState
import com.github.sceneren.compose.swipe.rememberSwipeRowState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSwipeTheme {
                CPMainPage()
            }

        }
    }
}


private sealed class MainEvent(val dragAnchors: DragAnchors) {
    data object CloseAll : MainEvent(DragAnchors.Center)

    data object OpenStart : MainEvent(DragAnchors.Start)

    data object OpenEnd : MainEvent(DragAnchors.End)
}

private val LocalMainEventFlow = staticCompositionLocalOf {
    MutableSharedFlow<MainEvent>()
}

private val colorList = listOf(
    Color(0xFF007BFF), // 蓝色
    Color(0xFF673AB7), // 深紫色
    Color(0xFF3F51B5), // 靛蓝色
    Color(0xFF009688), // 蓝绿色
    Color(0xFF4CAF50), // 绿色
    Color(0xFF8BC34A), // 浅绿色
    Color(0xFFCDDC39), // 青柠色
    Color(0xFFFFC107), // 琥珀色
    Color(0xFFFF9800), // 橙色
    Color(0xFF795548), // 棕色
)

@Composable
private fun rememberRandomColor(list: MutableList<Color>): Color = remember(list.size) {
    colorList[Random.nextInt(0, list.size)].apply { list.remove(this) }
}

private data class RowInfo(val state: SwipeRowState, val list: MutableList<Color>)

private val LocalRowInfo = compositionLocalOf<RowInfo> { error("") }

@Composable
private fun CPSwipeRow(
    state: SwipeRowState = rememberSwipeRowState(),
    startContent: (@Composable () -> Unit)? = { CPStartContent() },
    endContent: (@Composable () -> Unit)? = { CPEndContent() },
) {
    val list: MutableList<Color> = colorList.toMutableList()

    val eventFlow = LocalMainEventFlow.current

    LaunchedEffect(Unit) {
        eventFlow
            .onEach { state.animateTo(it.dragAnchors) }
            .launchIn(this)
    }

    val rowInfo = remember(state, list) {
        RowInfo(state, list)
    }

    CompositionLocalProvider(LocalRowInfo provides rowInfo) {
        SwipeRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = state,
            startContent = startContent,
            centerContent = { CPCenterContent() },
            endContent = endContent,
        )
    }
}

@Composable
private fun CPStartContent() {
    val (state, list) = LocalRowInfo.current

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .clickable { state.animateTo(scope, DragAnchors.End) }
            .background(rememberRandomColor(list))
            .fillMaxHeight()
            .width(45.dp),
        contentAlignment = Alignment.Center,
    ) {
        CPText("Open\nEnd")
    }
}

@Composable
private fun CPCenterContent() {
    val (state, list) = LocalRowInfo.current

    val anchorState = remember(state.offsetInfoState) {
        derivedStateOf { "anchor:${state.offsetInfoState.value.anchor.javaClass.simpleName}" }
    }

    val offsetState = remember(state.offsetInfoState) {
        derivedStateOf { "offset:${state.offsetInfoState.value.offset}" }
    }

    val totalState = remember(state.offsetInfoState) {
        derivedStateOf { "total:${state.offsetInfoState.value.total}" }
    }

    Column(
        modifier = Modifier
            .background(rememberRandomColor(list))
            .clickable { }
            .fillMaxWidth()
            .height(80.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CPCenterText(anchorState)
        CPCenterText(offsetState)
        CPCenterText(totalState)
    }
}

@Composable
private fun CPCenterText(state: State<String>) {
    CPText(text = state.value)
}

@Composable
private fun CPText(text: String) {
    Text(text = text, color = Color.White)
}

@Composable
private fun CPEndContent() {
    val (state, list) = LocalRowInfo.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxHeight()) {
        Box(
            modifier = Modifier
                .clickable {
                    state.animateTo(scope, DragAnchors.Start) {
                        Toast
                            .makeText(context, "11", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .background(rememberRandomColor(list))
                .fillMaxHeight()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            CPText("Open\nStart")
        }
        Box(
            modifier = Modifier
                .clickable { state.animateTo(scope, DragAnchors.Center) }
                .background(rememberRandomColor(list))
                .fillMaxHeight()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            CPText("Close\nEnd")
        }
    }
}

@Composable
private fun CPMainPage() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item("top") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                CPButton("Open Start", MainEvent.OpenStart)
                CPButton("Close All", MainEvent.CloseAll)
                CPButton("Open End", MainEvent.OpenEnd)
            }
        }
        item(key = 1) {
            CPSwipeRow(startContent = null)
        }
        item(key = 2) {
            CPSwipeRow(endContent = null)
        }
        item(key = 3) {
            CPSwipeRow()
        }
        item(key = 4) {
            CPSwipeRow(state = rememberSwipeRowState(positionalThreshold = { it * 0.2f }))
        }
        item(key = 5) {
            CPSwipeRow(state = rememberSwipeRowState(velocityThreshold = { 1000f }))
        }
    }
}

@Composable
private fun CPButton(text: String, event: MainEvent) {
    val eventFlow = LocalMainEventFlow.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .clickable { scope.launch { eventFlow.emit(event) } }
            .background(Color.Gray)
            .padding(8.dp),
    ) {
        CPText(text)
    }
}