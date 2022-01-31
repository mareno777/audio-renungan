package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song

@Composable
fun CategoriesSection(
    cardItems: List<Song>,
    modifier: Modifier = Modifier,
    onNavigationClick: (Song) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(cardItems.size) {
            CardItem(cardItems[it]) { category ->
                onNavigationClick(category)
            }
            if (it != cardItems.size - 1) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}