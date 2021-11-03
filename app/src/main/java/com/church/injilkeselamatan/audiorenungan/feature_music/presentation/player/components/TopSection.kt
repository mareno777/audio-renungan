package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun TopSection(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(
            Icons.Rounded.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    onBackClicked()
                },
            tint = MaterialTheme.colors.onSurface
        )

        Text(
            text = "Now Playing",
            fontFamily = sourceSansPro,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(26.dp)
        )
    }
}