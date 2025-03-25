package com.example.playaudiotest

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable


@Composable
fun AlbumItem(
    album: Album,
    viewModel: MainViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = album.audioList[0].albumArt,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clickable { navController.navigate(AlbumDetailScreen(album.albumName)) }
        )
        Text(
            text = album.albumName,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp
        )
        Text(
            text = album.artists,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp,
            color = Color.Gray
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.addToPlayList(album) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play this album"
                )
            }
            IconButton(
                onClick = { viewModel.addToPlayList(album.audioList) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = "Add this album to playlist"
                )
            }
        }
    }
}
