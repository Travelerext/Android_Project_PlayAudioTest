package com.example.playaudiotest

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val mediaReader: MediaReader,
    val player: ExoPlayer
): ViewModel() {

    init {
        initPlayer()
        startUpdatingCurrentPosition()
    }

    var playerControllerIcon by mutableStateOf(Icons.Filled.PlayArrow)
    var playState by mutableStateOf(false)
    var currentPosition by mutableLongStateOf(0L)

    var currentPlayAudio by mutableStateOf(
        AudioFile(
        contentUri = Uri.EMPTY,
        title = "Unknown",
        artists = "Unknown",
        duration = 0L,
        albumArt = Uri.parse("android.resource://com.example.playaudiotest/drawable/album")
        )
    )
        private set

    private val _loadingAudioFiles = MutableStateFlow(false)

    val loadingAudioFiles = _loadingAudioFiles
        .onStart { loadAudioFiles() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            false
        )
    var audioFiles by mutableStateOf(listOf<AudioFile>())
        private set
    private fun loadAudioFiles() {
        viewModelScope.launch {
            _loadingAudioFiles.value = true
            audioFiles = mediaReader.getAllAudioFiles()
            _loadingAudioFiles.value = false
        }
    }

    private fun startUpdatingCurrentPosition() {
        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    currentPosition = player.currentPosition
                }
                delay(1000L)
            }
        }
    }

    private fun initPlayer()
    {
        viewModelScope.launch{
            player.prepare()
            player.addListener(
                object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        playerControllerIcon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == Player.STATE_READY) {
                            audioFiles.find { it.contentUri == player.currentMediaItem?.localConfiguration?.uri }?.let { audioFile ->
                                currentPlayAudio = audioFile
                            }
                            currentPosition = player.currentPosition
                            playState = true
                        }
                        else if (playbackState == Player.STATE_ENDED) {
                            currentPlayAudio = AudioFile(
                                contentUri = Uri.EMPTY,
                                title = "Unknown",
                                artists = "Unknown",
                                duration = 0L,
                                albumArt = Uri.parse("android.resource://com.example.playaudiotest/drawable/album")
                            )
                            playState = false
                        }
                    }
                }
            )
        }
    }

    fun playAudio(uri: Uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
        player.play()
    }

    fun playAudioController() {
        if (player.isPlaying)
            player.pause()
        else
            player.play()
    }

    fun changePlayPosition(position: Long) {
        player.seekTo(position)
    }

    fun clearPlayAudios() {
        player.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}