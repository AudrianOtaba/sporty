package com.audrian.spotifyapp.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.metrics.PlaybackErrorEvent
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import com.audrian.spotifyapp.R
import com.audrian.spotifyapp.data.entity.other.Constants.NOTIFICATION_CHANNEL_ID
import com.audrian.spotifyapp.data.entity.other.Constants.NOTIFICATION_ID
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSession.Token,
    noticationListener: PlayerNoficationManager.NotificationListener,
    private val newSongCallbacks: () -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaController(context, sessionToken)
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            NOTIFICATION_ID,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply{
            setSmallIcon(R.drawable.ic_music)
            setMediaSessionToken(sessionToken)
        }
    }

    fun showNotification(player : Player){
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaController
    ) : PlayerNotificationManager.MediaDescriptionAdapter{

        override fun getCurrentContentTitle(player: player):CharSequence? {
              return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
              return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player : Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
              Glide.with(context).asBitmap()
                  .load(mediaController.metadata.description.iconUri)
                  .into(object : CustomTarget<Bitmap>(){
                      override fun onResourceReady(
                          resource: Bitmap,
                          transition: Transition<in Bitmap>?
                      ) {
                          callback.onBitmap(resource)
                      }

                      override fun onLoadCleared(placeholder: Drawable?) = Unit
                  })
               return null
        }
    }
}