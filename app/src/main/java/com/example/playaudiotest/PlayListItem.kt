package com.example.playaudiotest

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun PlayListItem(
    uri: Uri,
    index: Int,
    viewModel: MainViewModel
) {
    val file = viewModel.audioFiles.find { it.contentUri == uri }?: AudioFile(
        contentUri = Uri.EMPTY,
        title = "Unknown",
        artists = "Unknown",
        duration = 0L,
        albumArt = Uri.parse("android.resource://com.example.playaudiotest/drawable/album")
    )

    Row(
        modifier = Modifier
            .clickable {
                viewModel.changePlayingAudio(index)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = file.albumArt,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .weight(2f)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(6f)
        ) {
            Text(
                text = file.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.size(3.dp))

            Text(
                text = file.artists,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Box(
            modifier = Modifier.weight(2f)
        ) {
            var expanded by remember {
                mutableStateOf(false)
            }
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        Log.d("test", index.toString())
                        viewModel.deleteFromPlayList(index)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(3.dp))
                    Text(text = "Remove")
                }
            }
        }
    }
}