package com.church.injilkeselamatan.core.navigation

sealed class Screen(val route: String) {
    object AlbumsScreen : Screen("albums_screen")
    object PlayerScreen : Screen("player_screen")
    object EpisodeScreen : Screen("episode_screen")
    object DonationScreen : Screen("donation_screen")
}
