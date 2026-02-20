package ru.otus.cryptomvisample.features.coins

sealed interface CoinListAction {
    object LoadCoins : CoinListAction
    data class ToggleHighlightMovers(val isChecked: Boolean) : CoinListAction
    data class ToggleFavourite(val coinId: String) : CoinListAction
}
