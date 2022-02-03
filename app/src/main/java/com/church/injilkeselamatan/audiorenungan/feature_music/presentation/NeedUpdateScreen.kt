package com.church.injilkeselamatan.audiorenungan.feature_music.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NeedUpdateScreen() {
    val uriHandler = LocalUriHandler.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Aplikasi perlu diperbarui agar dapat digunakan",
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                uriHandler.openUri("https://play.google.com/store/apps/details?id=com.church.injilkeselamatan.audiorenungan")
            }) {
                Text(text = "Update Aplikasi", color =  MaterialTheme.colors.onBackground)
            }
        }
    }
}