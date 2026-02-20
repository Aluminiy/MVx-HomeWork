package ru.otus.cryptomvisample.features.coins.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.otus.cryptomvisample.features.coins.CoinListAction
import ru.otus.cryptomvisample.features.coins.CoinsScreenState
import ru.otus.cryptomvisample.ui.theme.TextPrimary

@Composable
fun CoinListScreen(
    state: CoinsScreenState,
    handleAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coins",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            FilterChip(
                onClick = { handleAction(CoinListAction.ToggleHighlightMovers(!state.highlightMovers)) },
                label = {
                    Text(
                        text = "Highlight Movers",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    if (state.highlightMovers) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Highlight Movers",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                selected = state.highlightMovers,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.categories.forEach { category ->
                item(span = { GridItemSpan(2) }) {
                    CategoryHeader(
                        title = category.name,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (category.coins.size >= 10) {
                    item(span = { GridItemSpan(2) }) {
                        HorizontalCoinList(
                            coins = category.coins,
                            onToggleFavourite = { coinId -> handleAction(CoinListAction.ToggleFavourite(coinId)) }
                        )
                    }
                } else {
                    itemsIndexed(
                        items = category.coins,
                        span = { index, coin -> GridItemSpan(1) },
                        key = { index, coin -> coin.id }
                    ) { index, coin ->
                        val (start, end) = if (index % 2 == 0) {
                            16.dp to 0.dp
                        } else {
                            0.dp to 16.dp
                        }
                        CoinCard(
                            coin = coin,
                            onToggleFavourite = { handleAction(CoinListAction.ToggleFavourite(coin.id)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = start, end = end)
                        )
                    }
                }
            }
        }
    }
}
