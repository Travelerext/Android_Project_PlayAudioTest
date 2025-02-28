package com.example.playaudiotest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay


@Composable
fun AudioListItem(
    file: AudioFile,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {

    val iconColor by remember {
        derivedStateOf {
            if (file.isInList.value) Color.Green else Color.Black
        }
    }

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
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = iconColor
            )
        }
    }
}
