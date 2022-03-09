package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audiorenungan.LocalSpacing
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun DonationScreen(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onBackground,
    iconSize: Dp = 35.dp,
    onCopyClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onEmailClicked: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
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
                color = Color.Gray,
                maxLines = 1
            )
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(0.3f)
                .padding(top = spacing.medium)
        ) {
            item {
                Button(
                    onClick = { onShareClicked() },
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = "Share Link",
                        color = Color.White,
                        style = MaterialTheme.typography.button
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(spacing.medium)) }
            item {
                Text(
                    text = "SHARE KE BANYAK ORANG",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.offset(y = (-12).dp)
                )
            }
            item { Spacer(modifier = Modifier.height(spacing.small)) }
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
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.offset(y = (-10).dp)
                )
            }
            item { Spacer(modifier = Modifier.height(spacing.medium)) }
            item {
                Image(
                    painter = painterResource(R.drawable.pray),
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
            item { Spacer(modifier = Modifier.height(spacing.medium)) }
            item {
                Image(
                    painter = painterResource(R.drawable.handshake),
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
            item { Spacer(modifier = Modifier.height(spacing.small)) }
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
            item { Spacer(modifier = Modifier.height(spacing.small)) }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "BCA 252 092 999 4",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onCopyClicked() },
                        modifier = Modifier
                            .size(22.dp)
                            .offset(x = 5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_content_copy),
                            contentDescription = "Salin nomor rekening",
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(spacing.medium)) }
            item {
                Image(
                    painter = painterResource(R.drawable.email),
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
                    color = if (isSystemInDarkTheme()) Color.White else Color.Blue,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            onEmailClicked()
                        }
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
