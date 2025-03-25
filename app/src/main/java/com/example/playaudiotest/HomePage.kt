package com.example.playaudiotest

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun HomePage(
    viewModel: MainViewModel,
) {
    val loadingAudioFiles by viewModel.loadingAudioFiles.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "More",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!loadingAudioFiles) {
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
        },
        drawerState = drawerState
    ) {
        Scaffold (
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else drawerState.close()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DensityMedium,
                                contentDescription = "More"
                            )
                        }
                    },
                    title = { Text("") }
                )
            },
            bottomBar = { BottomNavigationBar(viewModel) }
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
                        .size(200.dp)
                )
                ProgressBar(viewModel)
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
                            imageVector = viewModel.playControllerIcon,
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
                )
            }
        }
    }
}