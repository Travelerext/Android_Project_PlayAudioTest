package com.example.playaudiotest

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListSheet(
    viewModel: MainViewModel,
    modifier: Modifier
) {

    var expand by rememberSaveable {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(expand)

    IconButton(
        onClick = { expand = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
            contentDescription = null
        )
    }

    if (expand) {
        ModalBottomSheet(
            onDismissRequest = {
                expand = false
            },
            sheetState = sheetState
        ) {
            if (viewModel.playListState){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(viewModel.playList.toList()) { uri ->
                        PlayListItem(
                            uri,
                            viewModel.playList.indexOf(uri),
                            viewModel
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }
                TextButton(
                    onClick = {
                        viewModel.clearPlayAudios()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Remove all audios",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            } else {
                Text(
                    text = "Please add some audios",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}