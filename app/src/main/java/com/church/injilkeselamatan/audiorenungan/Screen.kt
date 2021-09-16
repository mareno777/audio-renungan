package com.church.injilkeselamatan.audiorenungan

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object PlayerScreen : Screen("player_screen")
    object EpisodeScreen : Screen("episode_screen")
    object ProfileScreen : Screen("profile_screen")
}
