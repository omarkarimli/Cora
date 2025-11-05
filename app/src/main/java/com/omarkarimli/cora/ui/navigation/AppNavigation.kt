package com.omarkarimli.cora.ui.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.omarkarimli.cora.R
import com.omarkarimli.cora.data.local.Converters
import com.omarkarimli.cora.ui.presentation.main.MainViewModel
import com.omarkarimli.cora.ui.presentation.screen.about.AboutScreen
import com.omarkarimli.cora.ui.presentation.screen.admin.AdminScreen
import com.omarkarimli.cora.ui.presentation.screen.auth.AuthScreen
import com.omarkarimli.cora.ui.presentation.screen.chat.ChatScreen
import com.omarkarimli.cora.ui.presentation.screen.chatHistory.ChatHistoryScreen
import com.omarkarimli.cora.ui.presentation.screen.fullscreenImageViewer.FullScreenImageViewerScreen
import com.omarkarimli.cora.ui.presentation.screen.guidelines.GuidelinesScreen
import com.omarkarimli.cora.ui.presentation.screen.profile.ProfileScreen
import com.omarkarimli.cora.ui.presentation.screen.settings.SettingsScreen
import com.omarkarimli.cora.ui.presentation.screen.splash.SplashScreen
import com.omarkarimli.cora.ui.presentation.screen.subscriptionhistory.SubscriptionHistoryScreen
import com.omarkarimli.cora.ui.presentation.screen.success.SuccessScreen
import com.omarkarimli.cora.ui.presentation.screen.usage.UsageScreen
import com.omarkarimli.cora.ui.presentation.screen.userSetup.UserSetupScreen
import com.omarkarimli.cora.ui.presentation.screen.upgrade.UpgradeScreen

sealed class Screen(val route: String, @StringRes val titleResId: Int) {
    data object Admin: Screen("admin", R.string.admin)
    data object Splash: Screen("splash", R.string.splash)
    data object Settings : Screen("settings", R.string.settings)
    data object About : Screen("about", R.string.about)
    data object Profile : Screen("profile", R.string.profile)
    data object Auth : Screen("auth", R.string.auth)
    data object UserSetup : Screen("userSetup", R.string.user_setup)
    data object Chat : Screen("chat", R.string.chat)
    data object ChatHistory: Screen("chatHistory", R.string.chat_history)
    data object FullScreenImageViewer: Screen("fullScreenImageViewer", R.string.full_screen_image_viewer)
    data object Success: Screen("success", R.string.success)
    data object Upgrade: Screen("upgrade", R.string.upgrade)
    data object SubscriptionHistory: Screen("subscriptionHistory", R.string.subscription_history)
    data object Usage: Screen("usage", R.string.usage)
    data object Guidelines: Screen("guidelines", R.string.guidelines)
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    initialShare: String? = null
) {
    val navController = rememberNavController()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold { innerPadding ->
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Admin.route) {
                    AdminScreen()
                }
                composable(Screen.Splash.route) {
                    SplashScreen()
                }
                composable(Screen.Auth.route) {
                    AuthScreen()
                }
                composable(
                    route = "${Screen.UserSetup.route}/{userModel}",
                    arguments = listOf(navArgument("userModel") {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val tag = Screen.UserSetup.route
                    val itemJson = backStackEntry.arguments?.getString("userModel")

                    itemJson?.let {
                        UserSetupScreen(Converters().toUserModel(it))
                    } ?: Log.e(tag, "Error: Navigation argument 'itemJson' is null.")
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(mainViewModel)
                }
                composable(Screen.About.route) {
                    AboutScreen()
                }
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
                composable(Screen.ChatHistory.route) {
                    ChatHistoryScreen()
                }
                composable(Screen.Success.route) {
                    SuccessScreen()
                }
                composable(Screen.Guidelines.route) {
                    GuidelinesScreen()
                }
                composable(Screen.Usage.route) {
                    UsageScreen()
                }
                composable(Screen.Upgrade.route) {
                    UpgradeScreen()
                }
                composable(Screen.SubscriptionHistory.route) {
                    SubscriptionHistoryScreen()
                }
                composable(
                    route = "${Screen.Chat.route}?chatHistoryId={chatHistoryId}&initialShare={initialShare}",
                    arguments = listOf(
                        navArgument("chatHistoryId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("initialShare") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val chatHistoryId = backStackEntry.arguments?.getString("chatHistoryId")
                    val arg = backStackEntry.arguments?.getString("initialShare")

                    ChatScreen(
                        chatHistoryId = chatHistoryId?.toIntOrNull(),
                        initialShare = initialShare ?: arg
                    )
                }
                composable(
                    route = "${Screen.FullScreenImageViewer.route}/{itemJson}?initialPage={initialPage}", // Changed itemJson to imagesJson for clarity and added optional initialPage
                    arguments = listOf(
                        navArgument("itemJson") { // Argument for the list of images
                            type = NavType.StringType
                        },
                        navArgument("initialPage") { // Optional argument for the starting index
                            type = NavType.IntType
                            defaultValue = 0 // Set a default value to make it optional
                        }
                    )
                ) { backStackEntry ->
                    val tag = Screen.FullScreenImageViewer.route
                    val imagesJson = backStackEntry.arguments?.getString("itemJson")
                    val initialPage = backStackEntry.arguments?.getInt("initialPage") ?: 0

                    imagesJson?.let {
                        FullScreenImageViewerScreen(
                            imageModels = Converters().toImageModels(it),
                            initialPage = initialPage
                        )
                    } ?: Log.e(tag, "Error: Navigation argument 'itemJson' is null.")
                }
            }
        }
    }
}
