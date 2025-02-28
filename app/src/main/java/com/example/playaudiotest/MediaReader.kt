package com.example.playaudiotest

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

class MediaReader(
    private val context: Context
) {
    fun getAllAudioFiles(): List<AudioFile> {
        val audioFiles = mutableListOf<AudioFile>()
        val queryUri = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else MediaStore.Audio.Media.getContentUri("external")
        context.contentResolver.query(
            queryUri,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistsIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex)
                val artists = cursor.getString(artistsIndex)
                val albumId = cursor.getLong(albumIdIndex)
                val duration = cursor.getLong(durationIndex)
                val albumsUri = if (Build.VERSION.SDK_INT >= 29) {
                    MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else MediaStore.Audio.Albums.getContentUri("external")
                val contentUri = ContentUris.withAppendedId(queryUri, id)
                audioFiles.add(
                    AudioFile(
                        contentUri = contentUri,
                        title = title?:"Unknown",
                        artists = artists?:"Unknown",
                        duration = duration,
                        albumArt = ContentUris.withAppendedId(albumsUri, albumId)
                    )
                )
            }
        }
        return audioFiles.toList()
    }
}