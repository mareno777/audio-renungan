package com.church.injilkeselamatan.audiorenungan.exoplayer

import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player

class MusicControlDispatcher : ControlDispatcher {

    private val TAG = this::class.java.simpleName

    override fun dispatchPrepare(player: Player): Boolean {
        player.prepare()
        return true
    }

    override fun dispatchSetPlayWhenReady(player: Player, playWhenReady: Boolean): Boolean {
        player.playWhenReady = playWhenReady
        return true
    }

    override fun dispatchSeekTo(player: Player, windowIndex: Int, positionMs: Long): Boolean {
        player.seekTo(windowIndex, positionMs)
        return true
    }

    override fun dispatchPrevious(player: Player): Boolean {
        player.previous()
        return true
    }

    override fun dispatchNext(player: Player): Boolean {
        player.next()
        return true
    }

    override fun dispatchRewind(player: Player): Boolean {
        return false
    }

    override fun dispatchFastForward(player: Player): Boolean {
        return false
    }

    override fun dispatchSetRepeatMode(player: Player, repeatMode: Int): Boolean {
        player.repeatMode = repeatMode
        return true
    }

    override fun dispatchSetShuffleModeEnabled(
        player: Player,
        shuffleModeEnabled: Boolean
    ): Boolean {
        return true
    }

    override fun dispatchStop(player: Player, reset: Boolean): Boolean {
        return false
    }

    override fun dispatchSetPlaybackParameters(
        player: Player,
        playbackParameters: PlaybackParameters
    ): Boolean {
        return true
    }

    override fun isRewindEnabled(): Boolean {
        return false
    }

    override fun isFastForwardEnabled(): Boolean {
        return false
    }
}