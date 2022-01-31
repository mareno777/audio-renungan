package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun DonationScreen(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onBackground,
    iconSize: Dp = 35.dp,
    onCopyClicked: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = sourceSansPro,
                color = if (isSystemInDarkTheme()) MaterialTheme.colors.onSurface else MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "\u00A9 Yosea Christiono",
                color = Color.Gray,
                maxLines = 1
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            item {
                Image(
                    painter = rememberImagePainter(data = R.drawable.share_icon),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
            item {
                Text(
                    text = "SHARE KE BANYAK ORANG",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Text(
                    text = """
                        supaya lebih banyak orang 
                        yang mencari kehendak TUHAN, 
                        menemukan tujuan hidup yang sejati,
                        dan bertumbuh dewasa dalam Kristus Yesus TUHAN
                    """.trimIndent(),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Image(
                    painter = rememberImagePainter(data = R.drawable.pray),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
            item {
                Text(
                    text = "DOAKAN",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Image(
                    painter = rememberImagePainter(data = R.drawable.handshake),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
            item {
                Text(
                    text = "DUKUNGAN",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
            }
            item {
                Text(
                    text = """
                        Rekening pelayanan Yosea Dwi Christiono
                    """.trimIndent(),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BCA 252 092 999 4",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onCopyClicked() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_content_copy),
                            contentDescription = "Salin nomor rekening",
                            tint = Color.Blue,
                            modifier = Modifier
                                .size(22.dp)
                                .offset(x = (-5).dp)
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Image(
                    painter = rememberImagePainter(data = R.drawable.email),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
            item {
                Text(
                    text = """
                        Apabila ada kesaksian yang ingin dibagikan silahkan hubungi:
                    """.trimIndent(),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            item {
                Text(
                    text = """
                        oasisjiwa2022@gmail.com
                    """.trimIndent(),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}
