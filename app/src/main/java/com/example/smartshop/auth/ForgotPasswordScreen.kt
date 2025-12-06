package com.example.smartshop.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartshop.ui.theme.SmartShopTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Réinitialiser le mot de passe",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        message?.let {
            Text(
                text = it,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    isLoading = true
                    isError = false
                    message = null
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                message = "Un email de réinitialisation a été envoyé à $email."
                            } else {
                                isError = true
                                message = task.exception?.message ?: "Une erreur s'est produite."
                            }
                            isLoading = false
                        }
                } else {
                    isError = true
                    message = "Veuillez entrer votre email."
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Envoyer l'email de réinitialisation")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { onNavigateToLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour à la connexion")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    SmartShopTheme {
        ForgotPasswordScreen(onNavigateToLogin = {})
    }
}
