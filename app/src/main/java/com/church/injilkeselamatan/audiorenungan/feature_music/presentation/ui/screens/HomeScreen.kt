package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.album
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.ui.sourceSansPro
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.viewmodels.HomeViewModel
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun HomeScreen(
    navController: NavController,
    mediaId: String,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    homeViewModel.mediaId = mediaId

    val constraints = ConstraintSet {
        val topSection = createRefFor("topSection")
        val categoriesSection = createRefFor("categoriesSection")
        val playingNowSection = createRefFor("playingNowSection")

        constrain(topSection) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(categoriesSection) {
            top.linkTo(topSection.bottom)
            bottom.linkTo(playingNowSection.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
        constrain(playingNowSection) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }

    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.fillMaxSize()
    ) {
        val mediaItems by homeViewModel.mediaItems.observeAsState(mutableListOf())
        TopHomeSection(modifier = Modifier.layoutId("topSection"), homeViewModel)
        CardCategoriesSection(
            cardItems = mediaItems,
            navController = navController,
            modifier = Modifier.layoutId("categoriesSection")
        )
        PlayingNowSectionHome(
            modifier = Modifier
                .layoutId("playingNowSection")
                .clickable {
                    // TODO: Open music player
                    navController.navigate(Screen.PlayerScreen.route)
                },
            viewModel = homeViewModel
        )
    }
}

@Composable
private fun TopHomeSection(modifier: Modifier = Modifier, viewModel: HomeViewModel) {


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Audio Renungan",
                fontFamily = sourceSansPro,
                color = if (isSystemInDarkTheme()) MaterialTheme.colors.onSurface else MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "\u00A9 JKI Injil Keselamatan",
                fontFamily = sourceSansPro,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        GlideImage(
            imageModel = "https://qph.fs.quoracdn.net/main-qimg-c7a526dfad7e78f9062521efd0a3ea70-c",
            requestOptions = RequestOptions()
                .override(144)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop(),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp),
        )
    }
}

@Composable
fun PlayingNowSectionHome(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val playbackStateCompat by viewModel.playbackStateCompat.observeAsState()
    val mediaMetadataCompat by viewModel.mediaMetadataCompat.observeAsState()

    val painter = when (mediaMetadataCompat?.album) {
        "Pohon Kehidupan" -> R.drawable.pohon_kehidupan
        "Belajar Takut Akan Tuhan" -> R.drawable.btat
        "Saat Teduh Bersama Tuhan" -> R.drawable.stbt
        else -> null
    }

    Surface(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (painter != null) {
                    GlideImage(
                        imageModel = painter,
                        requestOptions = RequestOptions()
                            .override(70)
                            .diskCacheStrategy(DiskCacheStrategy.DATA),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(70.dp)
                            .fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = mediaMetadataCompat?.title ?: "",
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mediaMetadataCompat?.artist ?: "",
                        style = MaterialTheme.typography.caption,
                        fontSize = 14.sp
                    )

                }
            }
            Icon(
                painterResource(
                    id = if (playbackStateCompat?.isPlaying == true) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play_arrow
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clickable {
                        if (playbackStateCompat?.isPlaying == true) {
                            viewModel.pause()
                        } else {
                            viewModel.play()
                        }
                    }
            )
        }
    }
}

@Composable
private fun CardCategoriesSection(
    cardItems: List<MediaItemData>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(cardItems.size) {
            CardItem(cardItems[it]) { category ->
                navController.navigate(Screen.EpisodeScreen.route + "/${category.title}/${category.mediaId}")
            }
            if (it != cardItems.size - 1) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}


@Composable
private fun CardItem(
    mediaItem: MediaItemData,
    modifier: Modifier = Modifier,
    onCardClicked: (MediaItemData) -> Unit
) {
    val painter =
        when (mediaItem.title) {
            "Pohon Kehidupan" -> R.drawable.pohon_kehidupan
            "Saat Teduh Bersama Tuhan" -> R.drawable.stbt
            else -> R.drawable.btat
        }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                onCardClicked(mediaItem)
            },
        shape = RoundedCornerShape(CornerSize(12.dp)),
        elevation = 8.dp
    ) {
//        Image(
//            painter = painter,
//            contentDescription = null,
//            contentScale = ContentScale.Crop
//        )

        GlideImage(
            imageModel = painter,
            requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA),
            contentScale = ContentScale.Crop
        )
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 0.0f,
                        endY = 1010f
                    )
                )
        ) {
            Text(
                text = mediaItem.title,
                fontSize = 24.sp,
                maxLines = 1,
                fontFamily = sourceSansPro,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}