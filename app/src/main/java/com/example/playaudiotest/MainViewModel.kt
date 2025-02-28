package com.example.playaudiotest

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val mediaReader: MediaReader,
    private val player: ExoPlayer
): ViewModel() {

    var playerControllerIcon by mutableStateOf(Icons.Filled.PlayArrow)
    var playListState by mutableStateOf(false)
    var playList = mutableListOf<Uri>()


    init {
        initPlayer()
    }

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

    var currentDuration by mutableLongStateOf(0L)
    private val _getCurrentPosition = MutableStateFlow(0L)

    val getCurrentPosition = _getCurrentPosition
        .onStart { updatingCurrentPosition() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0L
        )


    private fun updatingCurrentPosition() {
        viewModelScope.launch {
            while (true) {
                _getCurrentPosition.value = player.currentPosition
                currentDuration = player.duration
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

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == Player.STATE_ENDED) {
                            player.pause()
                            player.seekTo(0, 0)
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        playerControllerIcon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        audioFiles.find { it.contentUri == player.currentMediaItem?.localConfiguration?.uri }?.let { audioFile ->
                            currentPlayAudio = audioFile
                        }
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                        playListState = player.mediaItemCount > 0
                        playList.clear()
                        audioFiles.map { it.isInList.value = false }
                        for (i in 0 until player.mediaItemCount) {
                            player.getMediaItemAt(i).localConfiguration?.let { config ->
                                playList.add(config.uri)
                                audioFiles.find { config.uri == it.contentUri }?.isInList?.value = true
                            }
                        }
                    }
                }
            )
        }
    }

    fun addToPlayList(audioFile: AudioFile) {
        if (!audioFile.isInList.value) {
            player.addMediaItem(MediaItem.fromUri(audioFile.contentUri))
        }
    }

    fun changePlayingAudio(index: Int) {
        player.seekTo(index, 0)
        player.play()
    }

    fun deleteFromPlayList(index: Int) {
        player.removeMediaItem(index)
    }

    fun playAudioController() {
        if (player.isPlaying)
            player.pause()
        else
            player.play()
    }

    fun playNext() {
        player.seekToNext()
        player.play()
    }

    fun playPrevious() {
        player.seekToPrevious()
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