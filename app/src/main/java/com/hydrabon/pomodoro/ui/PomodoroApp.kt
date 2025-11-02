package com.hydrabon.pomodoro.ui

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hydrabon.pomodoro.navigation.Destinations
import com.hydrabon.pomodoro.ui.home.HomeScreen
import com.hydrabon.pomodoro.ui.home.HomeViewModel
import com.hydrabon.pomodoro.ui.onboarding.OnboardingMiuiScreen
import com.hydrabon.pomodoro.ui.settings.SettingsScreen
import com.hydrabon.pomodoro.ui.settings.SettingsViewModel
import com.hydrabon.pomodoro.ui.stats.StatsScreen
import com.hydrabon.pomodoro.ui.stats.StatsViewModel
import com.hydrabon.pomodoro.ui.theme.PomodoroTheme

@Composable
fun PomodoroApp() {
    PomodoroTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route
        val destinations = listOf(Destinations.HOME, Destinations.STATS, Destinations.SETTINGS)

        Scaffold(
            bottomBar = {
                if (currentRoute != Destinations.ONBOARDING_MIUI.route) {
                    NavigationBar {
                        destinations.forEach { destination ->
                            NavigationBarItem(
                                selected = currentRoute == destination.route,
                                onClick = {
                                    if (currentRoute != destination.route) {
                                        navController.navigate(destination.route) {
                                            popUpTo(Destinations.HOME.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                label = { Text(text = destination.route.replaceFirstChar { it.uppercase() }) },
                                icon = {}
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destinations.HOME.route,
                modifier = Modifier
            ) {
                composable(Destinations.HOME.route) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        onStart = viewModel::startTimer,
                        onPause = viewModel::pause,
                        onResume = viewModel::resume,
                        onCancel = viewModel::cancel,
                        onQuickSettingsGuide = {
                            navController.navigate(Destinations.ONBOARDING_MIUI.route)
                        }
                    )
                }
                composable(Destinations.STATS.route) {
                    val viewModel: StatsViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    StatsScreen(state = state)
                }
                composable(Destinations.SETTINGS.route) {
                    val viewModel: SettingsViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    SettingsScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onShowMiuiGuide = {
                            navController.navigate(Destinations.ONBOARDING_MIUI.route)
                        }
                    )
                }
                composable(Destinations.ONBOARDING_MIUI.route) {
                    OnboardingMiuiScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
