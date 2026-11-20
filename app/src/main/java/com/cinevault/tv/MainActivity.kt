package com.cinevault.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private enum class Screen { HOME, SEARCH, DETAILS, PLAYER }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineVaultTheme {
                var screen by remember { mutableStateOf(Screen.HOME) }
                var selected by remember { mutableStateOf(Catalog.movies.first()) }

                BackHandler(enabled = screen != Screen.HOME) {
                    screen = when (screen) {
                        Screen.PLAYER, Screen.DETAILS -> Screen.HOME
                        Screen.SEARCH -> Screen.HOME
                        else -> Screen.HOME
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF05070A)
                ) {
                    when (screen) {
                        Screen.HOME -> HomeScreen(
                            onSearch = { screen = Screen.SEARCH },
                            onOpen = {
                                selected = it
                                screen = Screen.DETAILS
                            }
                        )
                        Screen.SEARCH -> SearchScreen(
                            onOpen = {
                                selected = it
                                screen = Screen.DETAILS
                            }
                        )
                        Screen.DETAILS -> DetailScreen(
                            item = selected,
                            onPlay = { screen = Screen.PLAYER }
                        )
                        Screen.PLAYER -> PlayerScreen(
                            onClose = { screen = Screen.DETAILS }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CineVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color(0xFFE50914),
            background = Color(0xFF05070A),
            surface = Color(0xFF10151B)
        ),
        content = content
    )
}

@Composable
private fun HomeScreen(onSearch: () -> Unit, onOpen: (CatalogItem) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(48.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CINEVAULT",
                    color = Color(0xFFE50914),
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp
                )
                Button(onClick = onSearch) {
                    Text("Search")
                }
            }
        }
        item {
            FeaturedCard(Catalog.movies.first(), onOpen)
        }
        item {
            CatalogRow("Continue Watching", Catalog.continueWatching, onOpen)
        }
        item {
            CatalogRow("Trending", Catalog.trending, onOpen)
        }
    }
}

@Composable
private fun FeaturedCard(item: CatalogItem, onOpen: (CatalogItem) -> Unit) {
    Card(
        onClick = { onOpen(item) },
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(item.accent))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(item.accent), Color(item.accent).copy(alpha = 0.35f))
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.width(430.dp)) {
                Text(item.category.uppercase(), color = Color(0xFFFFD166), fontWeight = FontWeight.Bold)
                Text(item.title, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                Text(item.subtitle, color = Color.White.copy(alpha = 0.82f), fontSize = 18.sp)
                Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { onOpen(item) }, label = { Text("Play") })
                    AssistChip(onClick = { onOpen(item) }, label = { Text("${item.year}  •  ${item.rating}") })
                }
            }
        }
    }
}

@Composable
private fun CatalogRow(title: String, items: List<CatalogItem>, onOpen: (CatalogItem) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { item -> PosterCard(item, onOpen) }
        }
    }
}

@Composable
private fun PosterCard(item: CatalogItem, onOpen: (CatalogItem) -> Unit) {
    Card(
        onClick = { onOpen(item) },
        modifier = Modifier
            .width(190.dp)
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(item.accent))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp), contentAlignment = Alignment.BottomStart) {
            Column {
                Text(item.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("${item.year}  •  ${item.rating}", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(onOpen: (CatalogItem) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { Catalog.search(query) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Search", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Title or category") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF171D25),
                unfocusedContainerColor = Color(0xFF171D25)
            )
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(results) { item -> PosterCard(item, onOpen) }
        }
    }
}

@Composable
private fun DetailScreen(item: CatalogItem, onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(item.accent))
            .padding(64.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.width(520.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(item.category.uppercase(), color = Color(0xFFFFD166), fontWeight = FontWeight.Bold)
            Text(item.title, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
            Text("${item.year}  •  ${item.rating}  •  1h 48m", color = Color.White)
            Text(item.subtitle, color = Color.White.copy(alpha = 0.88f), fontSize = 19.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onPlay) { Text("▶  Play") }
                Button(onClick = {}) { Text("+  Watchlist") }
            }
        }
    }
}

@Composable
private fun PlayerScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            )
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    BackHandler(onBack = onClose)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                this.player = player
                useController = true
            }
        },
        update = { it.player = player }
    )
}
