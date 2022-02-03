package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.flow.collectLatest


@ExperimentalMaterialApi
@Composable
fun AlbumsScreen(
    navController: NavController,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { uiEvent ->
            when (uiEvent) {
                is AlbumViewModel.UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(uiEvent.message)
                }
            }
        }
    }
    val constraints = ConstraintSet {
        val topSection = createRefFor("topSection")
        val featuredSection = createRefFor("featuredSection")
        val categoriesSection = createRefFor("categoriesSection")

        constrain(topSection) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(featuredSection) {
            top.linkTo(topSection.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(categoriesSection.top)
        }
        constrain(categoriesSection) {
            top.linkTo(featuredSection.bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
    ) {

        val state by viewModel.state
        val playbackState by viewModel.playbackState().collectAsState()
        val mediaMetadata by viewModel.playingMetadata().collectAsState()
        val recentSong by viewModel.recentSong
        val featuredSongState by viewModel.featuredState

        Box {
            ConstraintLayout(
                constraintSet = constraints,
                modifier = Modifier.fillMaxSize()
            ) {
                TopAlbumsSection(modifier = Modifier.layoutId("topSection")) {
                    navController.navigate(Screen.DonationScreen.route)
                }
                FeaturedSongSection(
                    modifier = Modifier.layoutId("featuredSection"),
                    mediaMetadataCompat = mediaMetadata,
                    playbackState = playbackState,
                    featuredSongState = featuredSongState,
                    onRetryClicked = { viewModel.loadFeaturedSong() },
                    onPlayPauseClicked = { viewModel.onEvent(AlbumsEvent.PlayFeatured) }
                )
                when {
                    state.isLoading && state.songs.isEmpty() -> {
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
                    !state.isLoading && state.songs.isEmpty() -> {
                        ErrorSection(
                            modifier = Modifier.layoutId("categoriesSection"),
                            errorMessage = state.errorMessage ?: "Unknown error occurred",
                            onRetryClick = { viewModel.loadSongs() }
                        )
                    }
                }
            }
            PlayingNowSection(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
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
}

