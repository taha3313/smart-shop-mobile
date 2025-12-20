package com.example.smartshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartshop.auth.ForgotPasswordScreen
import com.example.smartshop.auth.LoginScreen
import com.example.smartshop.auth.SignUpScreen
import com.example.smartshop.ui.products.AddEditProductScreen
import com.example.smartshop.ui.products.ProductsScreen
import com.example.smartshop.ui.products.StatisticsScreen

@Composable
fun SmartShopNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { navController.navigate(Screen.Products.route) },
                onSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUp = { navController.navigate(Screen.Products.route) },
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Products.route) {
            ProductsScreen(
                onAddProduct = { navController.navigate(Screen.AddEditProduct.ROUTE) },
                onProductClick = { productId ->
                    navController.navigate(Screen.AddEditProduct(productId.toString()).route)
                },
                onStatsClick = { navController.navigate(Screen.Statistics.route) }
            )
        }
        composable(
            route = Screen.AddEditProduct.ROUTE + "?${Screen.AddEditProduct.ARG_PRODUCT_ID}={${Screen.AddEditProduct.ARG_PRODUCT_ID}}",
            arguments = listOf(navArgument(Screen.AddEditProduct.ARG_PRODUCT_ID) {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Screen.AddEditProduct.ARG_PRODUCT_ID)?.toLongOrNull()
            AddEditProductScreen(
                productId = productId,
                onProductAdded = { navController.popBackStack() }
            )
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
