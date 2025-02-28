package com.example.playaudiotest

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class AudioFile(
    val contentUri: Uri,
    val title: String,
    val artists: String,
    val duration: Long,
    val albumArt: Uri,
    var isInList: MutableState<Boolean> = mutableStateOf(false)
)
