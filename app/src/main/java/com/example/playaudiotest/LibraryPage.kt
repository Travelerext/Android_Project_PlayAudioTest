package com.example.playaudiotest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object LibraryScreen

@Serializable
data class AlbumDetailScreen (
    val albumName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryPage(
    viewModel: MainViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LibraryScreen
    ) {
        composable<LibraryScreen> {
            var expanded by rememberSaveable {
                mutableStateOf(false)
            }
            Scaffold (
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = null
                            )
                        },
                        title = {
                            Text(
                                text = "Audio Library",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                },
                bottomBar = { BottomNavigationBar(viewModel) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3)
                    ) {
                        items(viewModel.albumList) { item ->
                            AlbumItem(item, viewModel, navController)
                        }
                    }
                }
            }
        }
        composable<AlbumDetailScreen> {
            val args = it.toRoute<AlbumDetailScreen>()
            viewModel.albumList.find { albums -> albums.albumName == args.albumName }?.let { album ->
                AlbumDetailPage(album, viewModel, navController)
            }
        }
    }
}