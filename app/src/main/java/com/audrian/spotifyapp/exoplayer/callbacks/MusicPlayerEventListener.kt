package com.audrian.spotifyapp.exoplayer.callbacks

import android.widget.Toast
import com.audrian.spotifyapp.exoplayer.MusicService

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int){
        super.onplayerStateChanged(playWhenReady,playbackState)
        if (playbackState == player.STATE_READY && !playWhenReady) {
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An unknown error occured" , Toast.LENGTH_LONG).show()
    }
}