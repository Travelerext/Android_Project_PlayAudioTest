package com.example.playaudiotest

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun AudioTimeLine(viewModel: MainViewModel) {

    var timeLineLength by remember {
        mutableIntStateOf(0)
    }

    var offset by remember {
        mutableIntStateOf(0)
    }

    var currentPlayPosition by remember {
        mutableFloatStateOf(0f)
    }

    val state = rememberScrollableState { delta ->
        offset = (offset + delta).roundToInt().coerceIn(0, timeLineLength)
        viewModel.changePlayPosition((offset.toFloat() / timeLineLength * viewModel.currentPlayAudio.duration).roundToLong())
        delta
    }

    rememberCoroutineScope().also {
        it.launch {
            state.scroll {
                currentPlayPosition =
                    viewModel.currentPosition.toFloat() / viewModel.currentPlayAudio.duration.toFloat() * timeLineLength
                offset = currentPlayPosition.roundToInt()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(7f)
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.Gray, RoundedCornerShape(4.dp))
                    .onSizeChanged { size ->
                        timeLineLength = size.width - 6
                    }
            )
            Box(
                modifier = Modifier
                    .width(with(LocalDensity.current) {offset.toDp()} + 3.dp)
                    .height(4.dp)
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = offset, y = 0) }
                    .size(8.dp)
                    .background(Color.Black, CircleShape)
                    .align(Alignment.CenterStart)
                    .scrollable(
                        orientation = Orientation.Horizontal,
                        state = state
                    )
            )
        }
        Spacer(
            modifier = Modifier.size(5.dp)
        )
        Text(
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterVertically),
            text = "${formatTime(viewModel.currentPosition)}/${formatTime(viewModel.currentPlayAudio.duration)}",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun formatTime(duration: Long): String {
    val minute = duration/1000/60
    val second = duration/1000%60
    val result =
        if (duration >= 0)
            "%02d:%02d".format(minute, second)
        else
            "00:00"
    return result
}