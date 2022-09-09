package com.audrian.spotifyapp.plcoding.spotifyclone

import com.audrian.spotifyapp.data.entity.other.Constants.SONG_COLLECTION
import com.audrian.spotifyapp.data.entity.song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllsongs(): List<song>{
        return try {
            songCollection.get().await().toObjects(song::class.java)
        }catch (e: Exception){
            emptyList()
        }
    }
}