package ru.otus.cryptomvisample.features.coins

sealed interface CoinListChange {
    data class CoinsLoaded(val categories: List<CoinCategoryState>) : CoinListChange
    data class HighlightMovers(val isChecked: Boolean) : CoinListChange
}
