package com.church.injilkeselamatan.audiorenungan.feature_music.presentation

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.util.ConnectionLiveData
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.AlbumsScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.EpisodeScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.PlayerScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.theme.AudioRenunganTheme
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

            AudioRenunganTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.AlbumsScreen.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = Screen.AlbumsScreen.route) {
                            //val mediaId by mainViewModel.rootMediaId.observeAsState()
                            //mediaId?.let {
                            AlbumsScreen(navController = navController)
                            //}
                        }
                        composable(
                            route = Screen.EpisodeScreen.route + "/{album}",
                            arguments = listOf(
                                navArgument("album") {
                                    type = NavType.StringType
                                    nullable = true
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
                }
            }
        }
    }
}