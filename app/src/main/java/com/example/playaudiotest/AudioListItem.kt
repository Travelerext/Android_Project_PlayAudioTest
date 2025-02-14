package com.example.playaudiotest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioListItem(
    file: AudioFile,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = file.title,
            modifier = Modifier
                .clickable ( onClick = { viewModel.playAudio(file.contentUri) } )
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}