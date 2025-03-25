package com.example.playaudiotest

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressBar(viewModel: MainViewModel) {

    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors(
        thumbColor = Color.Black,
        activeTrackColor = Color.Black,
        disabledActiveTrackColor = Color.Gray
    )
    val sliderPosition = if (viewModel.currentDuration > 0) {
        viewModel.currentPosition.toFloat()/viewModel.currentDuration
    } else 0f

    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = Modifier
                .weight(7f)
        )
        {
            Slider(
                value = sliderPosition,
                onValueChange = { value ->
                    viewModel.changePlayPosition((value / 1 * viewModel.currentDuration).roundToLong())
                },
                interactionSource = interactionSource,
                colors = colors,
                thumb = {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(7.dp),
                        tint = Color.Black
                    )
                },
            )
        }
        Spacer(
            modifier = Modifier.size(5.dp)
        )
        Text(
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterVertically),
            text = "${formatTime(viewModel.currentPosition)}/${formatTime(viewModel.currentDuration)}",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun formatTime(duration: Long): String {
    val minute = (duration.toFloat()/1000).roundToLong()/60
    val second = (duration.toFloat()/1000).roundToLong()%60
    val result =
        if (duration >= 0)
            "%02d:%02d".format(minute, second)
        else
            "00:00"
    return result
}