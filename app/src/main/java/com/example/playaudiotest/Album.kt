package com.example.playaudiotest

import android.provider.MediaStore.Audio.Artists

data class Album(
    val albumName: String,
    val artists: String,
    val audioList: List<AudioFile>
)
