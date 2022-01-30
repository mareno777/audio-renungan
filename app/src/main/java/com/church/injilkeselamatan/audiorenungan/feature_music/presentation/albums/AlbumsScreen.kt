package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components.*
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun AlbumsScreen(
    navController: NavController,
    viewModel: AlbumViewModel = hiltViewModel()
) {

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val clipboard by remember {
        mutableStateOf(context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    }

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
        val playingNowSection = createRefFor("playingNowSection")

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

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            DonationScreen {
                val clip = ClipData.newPlainText("No Rekening", "2520929994")
                clipboard.setPrimaryClip(clip)
                scope.launch {
                    sheetState.hide()
                    scaffoldState.snackbarHostState
                        .showSnackbar("Nomor rekening berhasil disalin")
                }

            }
        },
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        BackHandler(sheetState.isVisible) {
            scope.launch {
                sheetState.animateTo(ModalBottomSheetValue.Hidden)
            }
        }
        Scaffold(
            scaffoldState = scaffoldState,
        ) {
            ConstraintLayout(
                constraintSet = constraints,
                modifier = Modifier.fillMaxSize()
            ) {

                val state by viewModel.state

                val playbackState by viewModel.playbackState().collectAsState()
                val mediaMetadata by viewModel.playingMetadata().collectAsState()
                val recentSong by viewModel.recentSong
                val featuredSongState by viewModel.featuredState

                TopAlbumsSection(modifier = Modifier.layoutId("topSection")) {
                    scope.launch {
                        sheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
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

                PlayingNowSection(
                    modifier = Modifier
                        .layoutId("playingNowSection")
                        .clickable {
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
}

