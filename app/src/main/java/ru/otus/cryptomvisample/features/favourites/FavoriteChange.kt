package ru.otus.cryptomvisample.features.favourites

sealed interface FavoriteChange {
    data class FavoritesLoaded(val favoriteCoins: List<FavouriteCoinState>) : FavoriteChange
}
