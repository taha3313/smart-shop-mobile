package com.example.smartshop.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Products : Screen("products")
    object Statistics : Screen("statistics")

    class AddEditProduct(productId: String? = null) : Screen(
        route = "${ROUTE}${if (productId != null) "?$ARG_PRODUCT_ID=$productId" else ""}"
    ) {
        companion object {
            const val ROUTE = "add_edit_product"
            const val ARG_PRODUCT_ID = "productId"
        }
    }
}
