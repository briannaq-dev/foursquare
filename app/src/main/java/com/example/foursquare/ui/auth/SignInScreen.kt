package com.example.foursquare.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Screen 1 — Sign In
 * Allows existing users to log in with email/password or Google.
 *
 * @param onSignInClick  Called when the "Sign in" button is tapped.
 * @param onSignUpClick  Called when the "Create an account" link is tapped.
 * @param onGoogleSignIn Called when the "Continue with Google" button is tapped.
 */
@Composable
fun SignInScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Replace with FourSquare logo
            Text(
                text  = "FourSquare",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = "Plan together, move together.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            Text(text = "Sign in", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                label         = { Text("Email") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value                = password,
                onValueChange        = { password = it },
                label                = { Text("Password") },
                singleLine           = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier             = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in")
            }

            Spacer(Modifier.height(8.dp))

            Text(text = "or", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick  = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO: Add Google logo icon
                Text("Continue with Google")
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = onSignUpClick) {
                Text("New here? Create an account")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    SignInScreen(
        onSignInClick  = {},
        onSignUpClick  = {},
        onGoogleSignIn = {}
    )
}