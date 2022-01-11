package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Dimensions
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.greaterThan
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.mediaQuery
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun TopAlbumsSection(modifier: Modifier = Modifier, onProfileClick: () -> Unit) {

    val context = LocalContext.current

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
        Image(
            painter = rememberImagePainter(
                request = ImageRequest.Builder(context)
                    .data("https://qph.fs.quoracdn.net/main-qimg-c7a526dfad7e78f9062521efd0a3ea70-c")
                    .transformations(CircleCropTransformation())
                    .build()

            ),
            contentDescription = "User profile",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp)
                .clickable {
                    onProfileClick()
                }
        )
    }
}