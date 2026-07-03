package com.umeetech.photofixai.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.screens.backgroundremover.BackgroundRemoverScreen
import com.umeetech.photofixai.presentation.screens.compress.CompressScreen
import com.umeetech.photofixai.presentation.screens.history.HistoryScreen
import com.umeetech.photofixai.presentation.screens.home.HomeScreen
import com.umeetech.photofixai.presentation.screens.onboarding.OnboardingScreen
import com.umeetech.photofixai.presentation.screens.passportphoto.PassportPhotoScreen
import com.umeetech.photofixai.presentation.screens.premium.PremiumScreen
import com.umeetech.photofixai.presentation.screens.productphoto.ProductPhotoScreen
import com.umeetech.photofixai.presentation.screens.resize.ResizeScreen
import com.umeetech.photofixai.presentation.screens.settings.SettingsScreen
import com.umeetech.photofixai.presentation.screens.signature.SignatureScreen
import com.umeetech.photofixai.presentation.screens.splash.SplashScreen
import com.umeetech.photofixai.presentation.screens.tools.ToolsScreen

/** Routes that display the bottom navigation bar. */
private val tabRoutes = BottomNavItem.items.map { it.route }.toSet()

@Composable
fun PhotoFixNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PhotoFixBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { navController.navigateToTab(it) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 12 } },
            exitTransition = { fadeOut(tween(180)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(220)) { it / 12 } }
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onOpenTool = { tool -> navController.navigate(Routes.forTool(tool)) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenPremium = { navController.navigateToTab(Routes.PREMIUM) },
                    onOpenHistoryItem = { navController.navigateToTab(Routes.HISTORY) }
                )
            }

            composable(Routes.TOOLS) {
                ToolsScreen(onOpenTool = { tool -> navController.navigate(Routes.forTool(tool)) })
            }

            composable(Routes.HISTORY) {
                HistoryScreen(
                    onBrowseTools = { navController.navigateToTab(Routes.TOOLS) }
                )
            }

            composable(Routes.PREMIUM) {
                PremiumScreen(onClose = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPremium = { navController.navigate(Routes.PREMIUM) }
                )
            }

            composable(Routes.BACKGROUND_REMOVER) {
                BackgroundRemoverScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.PASSPORT_PHOTO) {
                PassportPhotoScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.RESIZE) {
                ResizeScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.COMPRESS) {
                CompressScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.PRODUCT_PHOTO) {
                ProductPhotoScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SIGNATURE) {
                SignatureScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Suppress("unused")
private fun toolRoute(tool: ToolType) = Routes.forTool(tool)
