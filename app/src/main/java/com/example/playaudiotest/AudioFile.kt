package com.example.playaudiotest

import android.net.Uri

data class AudioFile(
    val contentUri: Uri,
    val title: String,
    val artists: String,
    val duration: Long,
    val albumArt: Uri,
)
