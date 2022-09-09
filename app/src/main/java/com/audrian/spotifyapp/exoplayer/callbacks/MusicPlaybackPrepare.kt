package com.audrian.spotifyapp.exoplayer.callbacks

import android.media.MediaMetadata
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import com.audrian.spotifyapp.exoplayer.FirebaseMusicSource
import java.sql.RowId

class MusicPlaybackPrepare(
    private val firebaseMusicSource: FirebaseMusicSource,
    private val playerPrepared: (MediaMetadata?) -> Unit
): MediaSessionConnector.PlaybackPreparer {

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?){
        firebaseMusicSource.whenReady {
            val itemToPlay = firebaseMusicSource.songs.find{ mediaId == it.description.mediaId}
            playerPrepared(itemToPlay)
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}