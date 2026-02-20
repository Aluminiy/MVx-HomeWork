package ru.otus.cryptomvisample.features.favourites

sealed interface FavoriteAction {
    object LoadFavorites : FavoriteAction
    data class RemoveFavorite(val coinId: String) : FavoriteAction
}
