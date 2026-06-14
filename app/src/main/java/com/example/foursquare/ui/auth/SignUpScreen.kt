package com.example.foursquare.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareTopBar

/**
 * Screen 2 — Sign Up
 * Collects full name, email, password, and password confirmation to create an account.
 *
 * @param onCreateAccount Called when the "Create account" button is tapped.
 * @param onBack          Called when the back arrow is tapped.
 */
@Composable
fun SignUpScreen(
    onCreateAccount: () -> Unit,
    onBack: () -> Unit
) {
    var fullName         by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var agreedToTerms    by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = "Create account",
                showBack = true,
                onBack   = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text  = "Join FourSquare to plan with friends",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value         = fullName,
                onValueChange = { fullName = it },
                label         = { Text("Full name") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value                = confirmPassword,
                onValueChange        = { confirmPassword = it },
                label                = { Text("Confirm password") },
                singleLine           = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier             = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked         = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it }
                )
                Text(
                    text  = "I agree to the Terms & Privacy Policy",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = onCreateAccount,
                modifier = Modifier.fillMaxWidth(),
                enabled  = agreedToTerms
            ) {
                Text("Create account")
            }
        }
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        onCreateAccount = {},
        onBack          = {}
    )
}
