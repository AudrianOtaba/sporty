package com.audrian.spotifyapp.exoplayer

import android.media.MediaMetadata
import android.media.MediaMetadata.*
import android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import androidx.core.net.toUri
import com.audrian.spotifyapp.exoplayer.State.*
import com.audrian.spotifyapp.plcoding.spotifyclone.MusicDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.withContext
import java.util.Collections.emptyList
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
){


     var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO){

        state = STATE_INITIALIZING
        val allSongs = musicDatabase.getAllsongs()
        songs =allSongs.map { song ->
            MediaMetadata.Builder()
                .putString(METADATA_KEY_ARTIST, song.subtitle)
                .putString(METADATA_KEY_MEDIA_ID, song.mediaId)
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
                .build()
        }
        state = STATE_INITIALISED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSource): ConcatenatingMediaSource{
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)

        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map{ song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowser.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()


    private var onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value){
            if (value== STATE_INITIALISED || value == STATE_ERROR){
                synchronized(onReadyListeners){
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALISED)
                    }
                }
            }else{
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit):Boolean{
         if (state == STATE_CREATED || state == STATE_INITIALIZING){
             onReadyListeners =+ onReadyListeners.plus(action)
             return false
         }else{
             action(state == STATE_INITIALISED)
             return true
         }
    }
}

enum class  State{
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALISED,
    STATE_ERROR
}