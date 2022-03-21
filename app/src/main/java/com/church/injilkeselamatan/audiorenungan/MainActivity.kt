package com.church.injilkeselamatan.audiorenungan

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ShareCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.kotlin.core.Amplify
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.NeedUpdateScreen
import com.church.injilkeselamatan.audio_presentation.albums.AlbumsScreen
import com.church.injilkeselamatan.audio_presentation.albums.components.DonationScreen
import com.church.injilkeselamatan.audio_presentation.episodes.EpisodeScreen
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.PlayerScreen
import com.church.injilkeselamatan.core.navigation.Screen
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.theme.AudioRenunganTheme
import com.church.injilkeselamatan.audiorenungan.feature_update.InAppUpdate
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagerApi
@AndroidEntryPoint
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var inAppUpdate: InAppUpdate

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)


        volumeControlStream = AudioManager.STREAM_MUSIC
        inAppUpdate = InAppUpdate(this)

        setContent {
            val systemUiController = rememberSystemUiController()
            val systemInDarkTheme = isSystemInDarkTheme()
            val needShare by mainViewModel.needShare.collectAsState(initial = false)

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
            val needUpdate by mainViewModel.needUpdate.collectAsState(initial = false)

            AudioRenunganTheme {
                if (needUpdate) {
                    NeedUpdateScreen()
                } else {
                    Box(Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.AlbumsScreen.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(route = Screen.AlbumsScreen.route) {
                                AlbumsScreen(
                                    navController = navController
                                )
                                //GoogleScreen()
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
                            composable(route = Screen.DonationScreen.route) {
                                DonationScreen(
                                    onCopyClicked = mainViewModel::copyToClipboard,
                                    onShareClicked = mainViewModel::shareIntent,
                                    onEmailClicked = mainViewModel::emailIntent
                                )
                            }
                        }
                    }
                }
            }
            if (needShare) {
                shareIntent()
                lifecycleScope.launchWhenCreated {
                    mainViewModel.needShare.emit(false)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    private suspend fun signIn() {
        try {
            val result = Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), this)
            if (result.isSignInComplete) {
                mainViewModel.fetchSession()
            } else {
                Log.e(TAG, "Signin cancelled by user")
            }
        } catch (error: Exception) {
            Log.e(TAG, "Signin failed $error")
        }
    }

    private fun shareIntent() {
        val text =
            """Download aplikasi Oasis Jiwa ini supaya lebih banyak orang hidup berjalan bersama TUHAN. https://injilkeselamatan.com/oasis-jiwa""".trimIndent()
        ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setChooserTitle("Share Oasis Jiwa")
            .setText(text)
            .startChooser()
    }
}