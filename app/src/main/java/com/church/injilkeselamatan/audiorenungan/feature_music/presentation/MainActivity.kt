package com.church.injilkeselamatan.audiorenungan.feature_music.presentation

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.EpisodeScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.screens.HomeScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.PlayerScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.theme.AudioRenunganTheme
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.viewmodels.MainViewModel
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.util.ConnectionLiveData
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPagerApi
@AndroidEntryPoint
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volumeControlStream = AudioManager.STREAM_MUSIC

        setContent {
            val navController = rememberNavController()
            val mainViewModel: MainViewModel = hiltViewModel()
            var colorInternetCheck by remember {
                mutableStateOf(Color.Black)
            }
            var internetInformationText by remember {
                mutableStateOf("")
            }

            val isNetworkConnected by connectionLiveData.observeAsState(Resource.Success(true))

            AudioRenunganTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.AlbumsScreen.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = Screen.AlbumsScreen.route) {
                            val mediaId by mainViewModel.rootMediaId.observeAsState()
                            mediaId?.let {
                                HomeScreen(navController = navController, mediaId = it)
                            }
                        }
                        composable(
                            route = Screen.EpisodeScreen.route + "/{album}/{parentId}",
                            arguments = listOf(
                                navArgument("album") {
                                    type = NavType.StringType
                                    nullable = true
                                },
                                navArgument("parentId") {
                                    type = NavType.StringType
                                    nullable = false
                                }
                            )
                        ) { entry ->
                            EpisodeScreen(
                                navController = navController,
                                album = entry.arguments?.getString("album"),
                                parentId = entry.arguments?.getString("parentId")
                            )
                        }
                        composable(route = Screen.PlayerScreen.route) {
                            PlayerScreen(navController)
                        }
                    }
                    AnimatedVisibility(
                        visible = colorInternetCheck != Color.Green,
                        enter = slideInVertically(animationSpec = spring()),
                        exit = slideOutVertically(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = internetInformationText,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = colorInternetCheck)
                                .align(Alignment.BottomCenter)
                        )
                    }
                    when (isNetworkConnected) {
                        is Resource.Loading -> {
                            colorInternetCheck = Color.Black
                            internetInformationText = "Checking internet connection..."
                        }
                        is Resource.Success -> {
                            colorInternetCheck = Color.Green
                            internetInformationText = "Connected to network"
                        }
                        is Resource.Error -> {
                            colorInternetCheck = Color.Red
                            internetInformationText = "No connection"
                        }
                    }
                }

            }
        }
    }
}