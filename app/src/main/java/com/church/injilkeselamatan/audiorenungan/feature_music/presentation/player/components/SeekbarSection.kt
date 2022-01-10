package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.widget.SeekBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.pager.ExperimentalPagerApi
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalPagerApi
@Composable
fun SeekbarSection(
    curPlayingPosition: Long?,
    curSongDuration: Long?,
    seekToPosition: (Long) -> Unit
) {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    val context = LocalContext.current

    var shouldUpdateSeekbar by remember {
        mutableStateOf(true)
    }
    var currentPositionText by remember {
        mutableStateOf("00:00")
    }
    val seekBarView = remember {
        SeekBar(context)
    }

    val seekBarListener = remember {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                currentPositionText = dateFormat.format(progress.toLong())
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                seekbar?.let {
                    seekToPosition(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        }
    }

    DisposableEffect(key1 = seekBarView) {
        seekBarView.setOnSeekBarChangeListener(seekBarListener)
        onDispose {
            seekBarView.setOnSeekBarChangeListener(null)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = currentPositionText,
            color = MaterialTheme.colors.onSurface
        )

        AndroidView(
            modifier = Modifier.weight(2f),
            factory = {
                seekBarView
            }
        ) { seekBarView ->

            seekBarView.max = curSongDuration?.toInt() ?: 0

            if (shouldUpdateSeekbar) {
                seekBarView.progress = curPlayingPosition?.toInt() ?: 0
            }
        }

        Text(
            text = dateFormat.format(curSongDuration ?: 0L),
            color = MaterialTheme.colors.onSurface
        )
    }
}