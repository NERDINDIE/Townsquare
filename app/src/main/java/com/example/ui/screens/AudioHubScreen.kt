package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.ui.plus.waves.TownsquareWavesApp

@Composable
fun AudioHubScreen(
    items: List<MediaItemEntity>,
    audioState: AudioState,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onOpenReader: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onTogglePlayPause: () -> Unit = {},
    onSeekNextStation: () -> Unit = {},
    onSeekPrevStation: () -> Unit = {},
    onOpenSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    TownsquareWavesApp(
        items = items,
        audioState = audioState,
        onPlayAudio = onPlayAudio,
        onBack = onOpenSidebar,
        modifier = modifier
    )
}
