package ru.otus.cryptomvisample

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.otus.common.di.findDependencies
import ru.otus.cryptomvisample.features.coins.CoinListViewModel
import ru.otus.cryptomvisample.features.coins.di.CoinListDependencies
import ru.otus.cryptomvisample.features.coins.di.DaggerCoinListComponent
import ru.otus.cryptomvisample.features.coins.ui.CoinListScreen
import ru.otus.cryptomvisample.features.favourites.FavoriteViewModel
import ru.otus.cryptomvisample.features.favourites.di.DaggerFavouritesComponent
import ru.otus.cryptomvisample.features.favourites.di.FavouritesDependencies
import ru.otus.cryptomvisample.features.favourites.ui.FavoriteCoinsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object CoinList : Screen("coin_list", "Coins", Icons.Default.List)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOf(Screen.CoinList, Screen.Favorites).forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(screen.icon, contentDescription = screen.title)
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.CoinList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.CoinList.route) {
                CoinListScreenContent()
            }
            composable(Screen.Favorites.route) {
                FavoriteCoinsScreenContent()
            }
        }
    }
}

@Composable
fun CoinListScreenContent() {
    val dependencies = LocalContext.current.findDependencies<CoinListDependencies>()
    val component = DaggerCoinListComponent.factory().create(dependencies)
    val viewModel: CoinListViewModel = viewModel(factory = component.viewModelFactory())
    val state by viewModel.state.collectAsState()
    CoinListScreen(
        state = state,
        handleAction = viewModel::handleAction
    )
}

@Composable
fun FavoriteCoinsScreenContent() {
    val dependencies = LocalContext.current.findDependencies<FavouritesDependencies>()
    val component = DaggerFavouritesComponent.factory().create(dependencies)
    val viewModel: FavoriteViewModel = viewModel(factory = component.viewModelFactory())
    val state by viewModel.state.collectAsState()

    FavoriteCoinsScreen(
        favoriteCoins = state.favoriteCoins,
        handleAction = viewModel::handleAction
    )
}
