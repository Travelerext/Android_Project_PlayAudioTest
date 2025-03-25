package com.example.playaudiotest

import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class MainViewModel(
    private val mediaReader: MediaReader,
): ViewModel() {

    private lateinit var mediaController: MediaController
    var playControllerIcon by mutableStateOf(Icons.Filled.PlayArrow)
    var playListState by mutableStateOf(false)
    var playList = mutableStateListOf<AudioFile>()
    var audioStateList = mutableStateListOf<Boolean>()
    var selectedItemIndex by mutableIntStateOf(0)

    fun getMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        initMediaController()
    }

    var currentPlayAudio by mutableStateOf(
        AudioFile(
            contentUri = Uri.EMPTY,
            title = "Unknown",
            artists = "Unknown",
            duration = 0L,
            albumArt = "android.resource://com.example.playaudiotest/drawable/album".toUri()
        )
    )

    private val _loadingAudioFiles = MutableStateFlow(false)

    val loadingAudioFiles = _loadingAudioFiles
        .onStart { loadAudioFiles() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000L),
            false
        )

    var audioFiles = mutableStateListOf<AudioFile>()
        private set

    var albumList = mutableStateListOf<Album>()

    private fun loadAudioFiles() {
        viewModelScope.launch {
            _loadingAudioFiles.value = false
            audioFiles.clear()
            audioFiles.addAll(mediaReader.getAllAudioFiles())
            albumList.clear()
            albumList.addAll(mediaReader.getAlbumList())
            repeat(audioFiles.size) {
                audioStateList.add(false)
            }
            _loadingAudioFiles.value = true
        }
    }

    var currentDuration by mutableLongStateOf(0L)
    var currentPosition by mutableLongStateOf(0L)
    val handler = Handler(Looper.getMainLooper())
    fun checkPlaybackPosition(delayMs: Long): Boolean =
        handler.postDelayed(
            {
                currentPosition = mediaController.currentPosition
                if (mediaController.isPlaying)
                    checkPlaybackPosition(delayMs)
            },
            delayMs)

    private fun initMediaController()
    {
        if (mediaController.playbackState == Player.STATE_ENDED) {
            mediaController.pause()
            mediaController.seekTo(0, 0)
        }
        playControllerIcon = if (mediaController.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
        checkPlaybackPosition(100L)
        mediaController.currentMediaItem?.let { mediaItem ->
            audioFiles.find { it.contentUri == mediaItem.localConfiguration?.uri }
                ?.let { audioFile ->
                    currentPlayAudio = audioFile
                }
            currentDuration = currentPlayAudio.duration
        } ?: { currentDuration = 0L }
        mediaController.addListener(
            object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED) {
                        mediaController.pause()
                        mediaController.seekTo(0, 0)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    playControllerIcon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                    if (isPlaying)
                        checkPlaybackPosition(100L)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
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
                    playListState = mediaController.mediaItemCount > 0
                    playList.clear()
                    audioStateList.fill(false)
                    for (i in 0 until mediaController.mediaItemCount) {
                        mediaController.getMediaItemAt(i).localConfiguration?.let { config ->
                            val audioFile = audioFiles.find { it.contentUri == config.uri }?:AudioFile(
                                contentUri = Uri.EMPTY,
                                title = "Unknown",
                                artists = "Unknown",
                                duration = 0L,
                                albumArt = "android.resource://com.example.playaudiotest/drawable/album".toUri()
                            )
                            playList.add(audioFile)
                            audioStateList[audioFiles.indexOf(audioFile)] = true
                        }
                    }
                }
            }
        )
    }

    fun addToPlayList(audioFile: AudioFile) {
        if (!audioStateList[audioFiles.indexOf(audioFile)]) {
            mediaController.addMediaItem(MediaItem.fromUri(audioFile.contentUri))
        }
    }

    fun addToPlayList(audioList: List<AudioFile>) {
        audioList.forEach{
            if (!audioStateList[audioFiles.indexOf(it)])
                mediaController.addMediaItem(MediaItem.fromUri(it.contentUri))
        }
    }

    fun addToPlayList(album: Album) {
        val items = mutableListOf<MediaItem>()
        album.audioList.forEach{ items.add(MediaItem.fromUri(it.contentUri)) }
        mediaController.setMediaItems(items)
        mediaController.play()
    }

    fun changePlayingAudio(index: Int) {
        mediaController.seekTo(index, 0)
        mediaController.play()
    }

    fun deleteFromPlayList(index: Int) {
        mediaController.removeMediaItem(index)
    }

    fun playAudioController() {
        if (mediaController.isPlaying)
            mediaController.pause()
        else
            mediaController.play()
    }

    fun playNext() {
        mediaController.seekToNext()
        mediaController.play()
    }

    fun playPrevious() {
        mediaController.seekToPrevious()
        mediaController.play()
    }

    fun changePlayPosition(position: Long) {
        mediaController.seekTo(position)
    }

    fun clearPlayAudios() {
        mediaController.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        mediaController.release()
    }
}