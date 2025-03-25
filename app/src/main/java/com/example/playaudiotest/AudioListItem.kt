package com.example.playaudiotest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
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
import coil.compose.AsyncImage


@Composable
fun AudioListItem(
    file: AudioFile,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .padding(16.dp),
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

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = file.artists,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        IconButton(
            onClick = {
                viewModel.addToPlayList(file)
            },
            modifier = Modifier.weight(2f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = null,
                tint = if (viewModel.audioStateList[viewModel.audioFiles.indexOf(file)]) Color.Green else Color.Black
            )
        }
    }
}
