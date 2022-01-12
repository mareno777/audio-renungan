package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Dimensions
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.greaterThan
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.mediaQuery
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun TopAlbumsSection(modifier: Modifier = Modifier, onProfileClick: () -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .mediaQuery(Dimensions.Height greaterThan 600.dp, modifier.padding(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = sourceSansPro,
                color = if (isSystemInDarkTheme()) MaterialTheme.colors.onSurface else MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "\u00A9 Yosea Christiono",
                fontFamily = sourceSansPro,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Harta Sorgawi",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    onProfileClick()
                }
        )
    }
}