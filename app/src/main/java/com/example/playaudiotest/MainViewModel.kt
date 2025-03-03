package com.example.playaudiotest

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var playList = mutableStateListOf<AudioFile>()
    var audioStateList = mutableStateListOf<Boolean>()

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
            repeat(audioFiles.size) {
                audioStateList.add(false)
            }
            _loadingAudioFiles.value = false
        }
    }

    var currentDuration by mutableLongStateOf(0L)
    private val _getCurrentPosition = MutableStateFlow(0L)
    val getCurrentPosition = _getCurrentPosition
        .asStateFlow()


    private fun updatingCurrentPosition() {
        viewModelScope.launch {
            while (player.isPlaying) {
                _getCurrentPosition.update {
                    player.currentPosition
                }
                delay(100L)
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
                        if (isPlaying)
                            updatingCurrentPosition()
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        _getCurrentPosition.update { 0L }
                        if (mediaItem != null){
                            audioFiles.find { it.contentUri == mediaItem.localConfiguration?.uri }
                                ?.let { audioFile ->
                                    currentPlayAudio = audioFile
                                }
                            currentDuration = currentPlayAudio.duration
                        } else {
                            currentDuration = 0L
                        }
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                        playListState = player.mediaItemCount > 0
                        playList.clear()
                        audioStateList.fill(false)
                        for (i in 0 until player.mediaItemCount) {
                            player.getMediaItemAt(i).localConfiguration?.let { config ->
                                val audioFile = audioFiles.find { it.contentUri == config.uri }?:AudioFile(
                                    contentUri = Uri.EMPTY,
                                    title = "Unknown",
                                    artists = "Unknown",
                                    duration = 0L,
                                    albumArt = Uri.parse("android.resource://com.example.playaudiotest/drawable/album")
                                )
                                playList.add(audioFile)
                                audioStateList[audioFiles.indexOf(audioFile)] = true
                            }
                        }
                    }
                }
            )
        }
    }

    fun addToPlayList(audioFile: AudioFile) {
        if (!audioStateList[audioFiles.indexOf(audioFile)]) {
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
        _getCurrentPosition.update { player.currentPosition }
    }

    fun clearPlayAudios() {
        player.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}