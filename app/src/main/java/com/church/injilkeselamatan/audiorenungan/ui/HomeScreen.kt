package com.church.injilkeselamatan.audiorenungan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.Screen
import com.church.injilkeselamatan.audiorenungan.data.models.Category
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.album
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.viewmodels.HomeViewModel

@ExperimentalCoilApi
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()) {

    val constraints = ConstraintSet {
        val topSection = createRefFor("topSection")
        val categoriesSection = createRefFor("categoriesSection")
        val playingNowSection = createRefFor("playingNowSection")

        constrain(topSection) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(categoriesSection) {
            top.linkTo(topSection.bottom)
            bottom.linkTo(playingNowSection.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
        constrain(playingNowSection) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }

    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.fillMaxSize()
    ) {
        TopHomeSection(modifier = Modifier.layoutId("topSection"))
        CardCategoriesSection(
            cardItems = listOf(
                Category(id = 1, name = "Pohon Kehidupan"),
                Category(id = 2, name = "Saat Teduh Bersama Tuhan"),
                Category(id = 3, name = "Belajar Takut Akan Tuhan"),
            ),
            navController = navController,
            modifier = Modifier.layoutId("categoriesSection")
        )
        PlayingNowSectionHome(
            modifier = Modifier
                .layoutId("playingNowSection")
                .clickable {
                    // TODO: Open music player
                    navController.navigate(Screen.PlayerScreen.route)
                },
            viewModel = homeViewModel
        )
    }
}

@ExperimentalCoilApi
@Composable
private fun TopHomeSection(modifier: Modifier = Modifier) {
    val painter = rememberImagePainter(
        data = "https://qph.fs.quoracdn.net/main-qimg-c7a526dfad7e78f9062521efd0a3ea70-c",
        builder = {
            transformations(CircleCropTransformation())
        }
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Audio Renungan",
                fontFamily = sourceSansPro,
                color = if (isSystemInDarkTheme()) MaterialTheme.colors.onSurface else MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "\u00A9 JKI Injil Keselamatan",
                fontFamily = sourceSansPro,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clickable {
                    //TODO: click to open account settings
                }
        )
    }
}


@Composable
fun PlayingNowSectionHome(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val playbackStateCompat by viewModel.playbackStateCompat.observeAsState()
    val mediaMetadataCompat by viewModel.mediaMetadataCompat.observeAsState()

    val painter = when (mediaMetadataCompat?.album) {
        "Pohon Kehidupan" -> R.drawable.pohon_kehidupan
        "Belajar Takut Akan Tuhan" -> R.drawable.btat
        "Saat Teduh Bersama Tuhan" -> R.drawable.stbt
        else -> null
    }?.let {
        painterResource(
            it
        )
    }

    Surface(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (painter != null) {
                    Image(
                        painter = painter,
                        modifier = Modifier
                            .width(70.dp)
                            .fillMaxHeight(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = mediaMetadataCompat?.title ?: "Unknown",
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mediaMetadataCompat?.artist ?: "Unknown",
                        style = MaterialTheme.typography.caption,
                        fontSize = 14.sp
                    )

                }
            }
            Icon(
                painterResource(
                    id = if (playbackStateCompat?.isPlaying == true) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play_arrow
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clickable {
                        if (playbackStateCompat?.isPlaying == true) {
                            viewModel.transportControls.pause()
                        } else {
                            viewModel.transportControls.play()
                        }
                    }
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun CardCategoriesSection(
    cardItems: List<Category>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(cardItems.size) {
            CardItem(cardItems[it]) { category ->
                navController.navigate(Screen.EpisodeScreen.route + "/${category.name}")
            }
            if (it != cardItems.size - 1) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun CardItem(
    card: Category,
    modifier: Modifier = Modifier,
    onCardClicked: (Category) -> Unit
) {
    val painter = rememberImagePainter(
        data = when (card.id) {
            1 -> R.drawable.pohon_kehidupan
            2 -> R.drawable.stbt
            else -> R.drawable.btat
        }
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                onCardClicked(card)
            },
        shape = RoundedCornerShape(CornerSize(12.dp)),
        elevation = 8.dp
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 0.0f,
                        endY = 1010f
                    )
                )
        ) {
            Text(
                text = card.name,
                fontSize = 24.sp,
                maxLines = 1,
                fontFamily = sourceSansPro,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}