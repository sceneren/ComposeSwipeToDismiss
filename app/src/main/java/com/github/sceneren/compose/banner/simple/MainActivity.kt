package com.github.sceneren.compose.banner.simple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.sceneren.compose.swipe.Banner
import com.github.sceneren.compose.swipe.PagerIndicator
import com.github.sceneren.compose.swipe.rememberBannerState
import com.github.sceneren.compose.banner.simple.theme.ComposeBannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeBannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val list = listOf(
        R.drawable.a1, R.drawable.a2,
        R.drawable.a3
    )

    var looper by remember {
        mutableStateOf(true)
    }

    val bannerState = rememberBannerState()
    val bannerState2 = rememberBannerState()

    val defaultIndicatorPainter = remember {
        ColorPainter(color = Color.White)
    }

    val selectIndicatorPainter = remember {
        ColorPainter(color = Color(0xFFF95521))
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {

        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Banner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    pageCount = list.size,
                    bannerState = bannerState
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(list[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                PagerIndicator(
                    modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
                    size = list.size,
                    offsetPercentWithSelectFlow = bannerState.createChildOffsetPercentFlow(),
                    selectIndexFlow = bannerState.createCurrSelectIndexFlow(),
                    indicatorItem = {
                        Image(
                            modifier = Modifier
                                .size(width = 4.dp, height = 3.dp)
                                .clip(RoundedCornerShape(100)),
                            painter = defaultIndicatorPainter,
                            contentDescription = null
                        )
                    },
                    selectIndicatorItem = {
                        Image(
                            modifier = Modifier
                                .size(width = 10.dp, height = 3.dp)
                                .clip(RoundedCornerShape(100)),
                            painter = selectIndicatorPainter,
                            contentDescription = null
                        )
                    }
                )
            }

        }

        item {
            Banner(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                pageCount = list.size,
                bannerState = bannerState2,
                contentPadding = PaddingValues(horizontal = 20.dp),
                animScale = 0.85f,
                pageSpacing = 10.dp,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(list[index]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }

        items(30) {
            Button({
                looper = !looper
            }) {
                Text("切换")
            }
        }


    }


}