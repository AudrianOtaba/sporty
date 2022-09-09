package com.audrian.spotifyapp.di

import android.content.Context
import android.media.AudioAttributes
import com.audrian.spotifyapp.plcoding.spotifyclone.MusicDatabase
import com.squareup.okhttp.internal.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import java.security.cert.CRLReason

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideMusicDatabase() = MusicDatabase()

    @ServiceScoped
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    @ServiceScoped
    @Provides
    fun provideExoplayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = SimpleExoplayer.Builder(context).build().apply{
        setAudioAttributes(audioAttributes,true)
        setHanleAudioBecomingNoisy(true)
    }

    @ServiceScoped
    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    ) = DefaultDataSourceFactory(context, Util.getUserAgent(context,"Spotify App "))

}