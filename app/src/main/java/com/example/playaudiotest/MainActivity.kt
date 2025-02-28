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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.motionEventSpy
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
                    return MainViewModel(mediaReader,player) as T
                }
            }
        }
    )

    @OptIn(ExperimentalMaterial3Api::class)
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
                MoreDrawer(
                    context = applicationContext,
                    modifier = Modifier.fillMaxSize(),
                    viewModel
                ) { innerPadding ->
                    val scrollState = rememberScrollState()
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (viewModel.playListState) {
                            AsyncImage(
                                model = viewModel.currentPlayAudio.albumArt,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(300.dp)
                            )
                            Spacer(Modifier.size(16.dp))
                            Text(
                                text = viewModel.currentPlayAudio.title,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        } else Text(
                            text = "Nothing to play.",
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(350.dp)
                        )
                        AudioTimeLine(viewModel)
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.playPrevious()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.playAudioController()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    painter = rememberVectorPainter(viewModel.playerControllerIcon),
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.playNext()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = null
                                )
                            }
                        }
                        PlayListSheet(
                            viewModel,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

