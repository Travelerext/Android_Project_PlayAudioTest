package com.example.playaudiotest

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.playaudiotest.ui.theme.PlayAudioTestTheme

class MainActivity : ComponentActivity() {

    private val mediaReader by lazy {
        MediaReader(applicationContext)
    }

    private val player by lazy {
        ExoPlayer.Builder(applicationContext).build()
    }

    private val viewModel by viewModels<MainViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(mediaReader, player) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val permissions = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        ActivityCompat.requestPermissions(
            this,
            permissions,
            0
        )

        setContent {
            PlayAudioTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val loadingAudioFiles by viewModel.loadingAudioFiles.collectAsStateWithLifecycle()
                    val scrollState = rememberScrollState()
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (viewModel.playState) {
                            AsyncImage(
                                model = viewModel.currentPlayAudio.albumArt,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(300.dp)
                            )
                            Text(
                                text = viewModel.currentPlayAudio.title,
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Column(
                                modifier = Modifier
                                    .width(200.dp)
                                    .horizontalScroll(scrollState)
                            ) {
                                Text(
                                    text = viewModel.currentPlayAudio.artists,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            AudioTimeLine(viewModel)
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.playAudioController()
                                    },
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Icon(
                                        rememberVectorPainter(viewModel.playerControllerIcon),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.clearPlayAudios()
                                    },
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Stop,
                                        contentDescription = null
                                    )
                                }
                            }
                        } else Text(
                            text = "Nothing to play.",
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .size(400.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        if (loadingAudioFiles) {
                            CircularProgressIndicator()
                        }
                        else {
                            LazyColumn (modifier = Modifier.fillMaxWidth()) {
                                items(viewModel.audioFiles) {
                                    AudioListItem(
                                        file = it,
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

