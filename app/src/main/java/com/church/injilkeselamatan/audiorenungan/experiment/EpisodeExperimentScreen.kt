package com.church.injilkeselamatan.audiorenungan.experiment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.ui.SongItem
import com.church.injilkeselamatan.audiorenungan.ui.productSansGoogle
import com.church.injilkeselamatan.audiorenungan.util.Constants
import com.church.injilkeselamatan.audiorenungan.viewmodels.EpisodeViewModel

@Composable
fun EpisodeExperimentScreen(
    navController: NavController,
    album: String?,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {
    var songs by episodeViewModel.songList
    var isExpanded by remember {
        mutableStateOf(false)
    }
    songs = songs.filter { it.album == album }

    val description = when (album) {
        "Pohon Kehidupan" -> Constants.DESC_PK
        "Belajar Takut Akan Tuhan" -> Constants.DESC_BTAT
        "Saat Teduh Bersama Tuhan" -> Constants.DESC_STBT
        else -> ""
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = album ?: "",
                    style = MaterialTheme.typography.h4,
                    fontFamily = productSansGoogle,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = description,
                        fontFamily = productSansGoogle,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                    )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            isExpanded = !isExpanded
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (isExpanded) "show less" else "show more",
                        color = Color(0xFF0073FF),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF0073FF)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Episodes",
                    fontFamily = productSansGoogle,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Divider()
            }
            items(songs) { song ->
                SongItem(song = song) {
                    episodeViewModel.playMediaId(song.id)
                }
                Divider()
            }
        }
    }
}