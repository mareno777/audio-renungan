package com.church.injilkeselamatan.audiorenungan.feature_music.presentation

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.kotlin.core.Amplify
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.util.ConnectionLiveData
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.AlbumsScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.EpisodeScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.PlayerScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.theme.AudioRenunganTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagerApi
@AndroidEntryPoint
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    private val mainViewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
//                mainViewModel.fetchSession()
//            }
//        }
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)


        volumeControlStream = AudioManager.STREAM_MUSIC

        setContent {
            val systemUiController = rememberSystemUiController()
            val systemInDarkTheme = isSystemInDarkTheme()

            SideEffect {
                // Update all of the system bar colors to be transparent, and use
                // dark icons if we're in light theme
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !systemInDarkTheme
                )
            }

            LaunchedEffect("signIn") {
                mainViewModel.needSignIn.collect { needSignIn ->
                    if (needSignIn) {
                        signIn()
                    }
                }
            }


            val navController = rememberNavController()

            AudioRenunganTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.AlbumsScreen.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = Screen.AlbumsScreen.route) {
                            AlbumsScreen(navController = navController)
                        }
                        composable(
                            route = Screen.EpisodeScreen.route + "/{album}",
                            arguments = listOf(
                                navArgument("album") {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )
                        ) {
                            EpisodeScreen(
                                navController = navController
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
    private suspend fun signIn() {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), this)
            if (result.isSignInComplete) {
                mainViewModel.isLoading.emit(false)
            }
        } catch (error: AuthException) {
            Log.e("AuthQuickStart", "Signin failed", error)
        }
    }
}