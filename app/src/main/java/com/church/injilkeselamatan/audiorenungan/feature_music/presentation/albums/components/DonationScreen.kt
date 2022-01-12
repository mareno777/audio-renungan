package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audiorenungan.R

@Composable
fun DonationScreen(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onBackground,
    textSize: TextUnit = 16.sp,
    onCopyClicked: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Harta Sorgawi",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = """
                         "Janganlah kamu mengumpulkan harta di bumi; di bumi ngengat dan karat 
                         merusakkannya dan pencuri membongkar serta mencurinya.
                         Tetapi kumpulkanlah bagimu harta di sorga; di sorga ngengat dan karat
                         tidak merusakkannya dan pencuri tidak membongkar serta mencurinya.
                          (Matius 6:19-20)
                    """.trimIndent(),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = textColor,
                    style = MaterialTheme.typography.body2,
                    fontSize = textSize
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = """
                        Apabila saudara mau mendukung pelayanan Renungan Audio ini,
                        supaya renungan audio ini bisa menolong lebih banyak orang
                        untuk hidup dalam Kehendak TUHAN dan hidup takut akan TUHAN.
                    """.trimIndent(),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = " Rekening pelayanan a/n Yosea Dwi Christiono",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BCA 252 082 7172",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            onCopyClicked()
                        }) {
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
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = "Tuhan Yesus memberkati.",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}
