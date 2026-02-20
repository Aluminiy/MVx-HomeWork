package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    init {
        handleAction(FavoriteAction.LoadFavorites)
    }

    fun handleAction(action: FavoriteAction) {
        // Dispatcher
        when (action) {
            is FavoriteAction.LoadFavorites -> loadFavoriteCoins()
            is FavoriteAction.RemoveFavorite -> removeFavourite(action.coinId)
        }
    }

    private fun dispatch(change: FavoriteChange) {
        _state.update { currentState ->
            reduce(currentState, change)
        }
    }

    private fun reduce(currentState: FavoriteCoinsScreenState, change: FavoriteChange): FavoriteCoinsScreenState {
        // Reducer
        return when (change) {
            is FavoriteChange.FavoritesLoaded -> {
                currentState.copy(favoriteCoins = change.favoriteCoins)
            }
        }
    }

    private fun removeFavourite(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
    }

    private fun loadFavoriteCoins() {
        consumeFavoriteCoinsUseCase()
            .map { favoriteCoins ->
                favoriteCoins.map { coin ->
                    mapper.mapToState(coin)
                }
            }
            .onEach { favoriteCoinsState ->
                dispatch(FavoriteChange.FavoritesLoaded(favoriteCoinsState))
            }
            .launchIn(viewModelScope)
    }
}
