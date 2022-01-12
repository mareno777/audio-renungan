package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components.*
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen


@Composable
fun AlbumsScreen(
    navController: NavController,
    viewModel: AlbumViewModel = hiltViewModel()
) {


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


        val state by viewModel.state

        val playbackState by viewModel.playbackState().collectAsState()
        val mediaMetadata by viewModel.playingMetadata().collectAsState()
        val recentSong by viewModel.recentSong

        TopAlbumsSection(modifier = Modifier.layoutId("topSection")) {
            // TODO: Open account settings
        }
        when {
            state.isLoading -> {
                LoadingSection(modifier = Modifier.layoutId("categoriesSection"))
            }
            state.songs.isNotEmpty() -> {
                CategoriesSection(
                    cardItems = state.songs,
                    modifier = Modifier.layoutId("categoriesSection")
                ) { category ->
                    navController.navigate(Screen.EpisodeScreen.route + "/${category.album}")
                }
            }
            state.errorMessage?.isNotEmpty() == true -> {
                ErrorSection(
                    modifier = Modifier.layoutId("categoriesSection"),
                    errorMessage = state.errorMessage!!
                ) {
                    viewModel.loadSongs()
                }
            }
        }

        PlayingNowSection(
            modifier = Modifier
                .layoutId("playingNowSection")
                .clickable {
                    // TODO: Open music player
                    navController.navigate(Screen.PlayerScreen.route)
                },
            playbackStateCompat = playbackState,
            mediaMetadataCompat = if (mediaMetadata == NOTHING_PLAYING) recentSong else mediaMetadata
        ) { needToPlay ->
            viewModel.onEvent(
                AlbumsEvent.PlayOrPause(
                    isPlay = needToPlay
                )
            )
        }
    }
}

