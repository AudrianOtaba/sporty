package com.audrian.spotifyapp.exoplayer.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.audrian.spotifyapp.data.entity.other.Constants.NOTIFICATION_ID
import com.audrian.spotifyapp.exoplayer.MusicService

class MusicPlayerNotificaticationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int , dismissedByUser:Boolean) {
         super.onNotificationCancelled(notificationId , dismissedByUser)
         musicService.apply{
             stopForegroud(true)
             isForegroundService = false
             stopSelf()
         }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
         super.onNotificationPosted(notificationId,notification,ongoing)
        musicService.apply{
            if (ongoing && ! isForegroundService){
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this:: class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }
    }

}