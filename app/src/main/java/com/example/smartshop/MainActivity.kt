package com.example.smartshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartshop.auth.LoginScreen
import com.example.smartshop.auth.SignUpScreen
import com.example.smartshop.ui.theme.SmartShopTheme

sealed class Screen {
    object Login : Screen()
    object SignUp : Screen()
    object Home : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartShopTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = Screen.Home },
                                onNavigateToSignUp = { currentScreen = Screen.SignUp }
                            )
                        }
                        is Screen.SignUp -> {
                            SignUpScreen(
                                onSignUpSuccess = { currentScreen = Screen.Home },
                                onNavigateToLogin = { currentScreen = Screen.Login }
                            )
                        }
                        is Screen.Home -> {
                            HomeScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenue sur l'écran d'accueil!", modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartShopTheme {
        HomeScreen()
    }
}