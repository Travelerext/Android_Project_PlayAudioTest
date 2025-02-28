package com.example.playaudiotest

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun MoreDrawer(
    context: Context,
    modifier: Modifier,
    viewModel: MainViewModel,
    content: @Composable (PaddingValues) -> Unit
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
            }
        ) { innerPadding ->
                content(innerPadding)
        }
    }
}