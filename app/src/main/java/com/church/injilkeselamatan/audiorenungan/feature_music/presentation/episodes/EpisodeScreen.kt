package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.productSansGoogle
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.viewmodels.EpisodeViewModel
import com.church.injilkeselamatan.audiorenungan.di.Constants
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun EpisodeScreen(
    navController: NavController,
    parentId: String?,
    album: String?,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {
    val songs by episodeViewModel.mediaItems.observeAsState(mutableListOf())
    var isExpanded by remember {
        mutableStateOf(false)
    }
    //songs.filter { it.mediaId == album }

    LaunchedEffect(parentId) {
        parentId?.let {
            episodeViewModel.subscribe(it)
        }
    }

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
                SongItem(song = song, episodeViewModel)
                Divider()
            }
        }
    }
}

@Composable
fun SongItem(song: MediaItemData, episodeViewModel: EpisodeViewModel) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .clickable { episodeViewModel.playMediaId(song.mediaId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

//            Image(
//                painter = rememberImagePainter(
//                    data = song.albumArtUri
//                ), modifier = Modifier.size(60.dp),
//                contentDescription = null,
//                contentScale = ContentScale.FillBounds
//            )
            GlideImage(
                imageModel = song.albumArtUri,
                requestOptions = RequestOptions()
                    .override(60)
                    .diskCacheStrategy(DiskCacheStrategy.DATA),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(60.dp)

            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = song.subtitle,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )

            }
        }
        val maxProgress by episodeViewModel.maxProgress.observeAsState(0)
        val downloadedLength by episodeViewModel.downloadedLength.observeAsState(0)
        AndroidView(
            factory = {
                CircularProgressBar(it)
            },
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // TODO: click to download media
                    episodeViewModel.downloadSong(song.mediaId)
                    episodeViewModel.maxProgressDownload(song.mediaId)
                }
        ) { circularProgress ->

            maxProgress?.let {
                circularProgress.progressMax = maxProgress.toFloat()
                circularProgress.progress = downloadedLength.toFloat()
            }
        }
        Icon(
            painterResource(
                id = R.drawable.download
            ),
            contentDescription = "Download this media",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // TODO: click to download media
                    episodeViewModel.downloadSong(song.mediaId)
                }
        )
    }
}