package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private var fullCategories: List<CoinCategoryState> = emptyList()

    init {
        handleAction(CoinListAction.LoadCoins)
    }

    fun handleAction(action: CoinListAction) {
        // Dispatcher
        when (action) {
            is CoinListAction.LoadCoins -> requestCoins()
            is CoinListAction.ToggleHighlightMovers -> {
                dispatch(CoinListChange.HighlightMovers(action.isChecked))
            }
            is CoinListAction.ToggleFavourite -> toggleFavourite(action.coinId)
        }
    }

    private fun dispatch(change: CoinListChange) {
        _state.update { currentState ->
            reduce(currentState, change)
        }
    }

    private fun reduce(currentState: CoinsScreenState, change: CoinListChange): CoinsScreenState {
        // Reducer
        return when (change) {
            is CoinListChange.CoinsLoaded -> {
                fullCategories = change.categories
                currentState.copy(
                    categories = applyHighlight(fullCategories, currentState.highlightMovers)
                )
            }
            is CoinListChange.HighlightMovers -> {
                currentState.copy(
                    highlightMovers = change.isChecked,
                    categories = applyHighlight(fullCategories, change.isChecked)
                )
            }
        }
    }

    private fun applyHighlight(
        categories: List<CoinCategoryState>,
        highlightMovers: Boolean
    ): List<CoinCategoryState> {
        return categories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(highlight = highlightMovers && coin.isHotMover)
            })
        }
    }

    private fun toggleFavourite(coinId: String) {
        val isCurrentlyFavorite = fullCategories.any { category ->
            category.coins.any { coin -> coin.id == coinId && coin.isFavourite }
        }
        
        if (isCurrentlyFavorite) {
            unsetFavouriteCoinUseCase(coinId)
        } else {
            setFavouriteCoinUseCase(coinId)
        }
    }

    private fun requestCoins() {
        consumeCoinsUseCase()
            .map { categories ->
                categories.map { category -> coinsStateFactory.create(category) }
            }
            .onEach { categoryListState ->
                dispatch(CoinListChange.CoinsLoaded(categoryListState))
            }
            .catch {
                dispatch(CoinListChange.CoinsLoaded(emptyList()))
            }
            .launchIn(viewModelScope)
    }
}
