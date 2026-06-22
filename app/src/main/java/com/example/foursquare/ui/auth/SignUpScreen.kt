package com.example.foursquare.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foursquare.ui.common.FourSquareTopBar

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = viewModel(),
    onCreateAccount: () -> Unit,
    onBack: () -> Unit
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms   by remember { mutableStateOf(false) }

    val authState    by authViewModel.authState.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    // Passwords must match locally before we call Firebase
    val passwordsMatch = password == confirmPassword
    val canSubmit = fullName.isNotBlank()
            && email.isNotBlank()
            && password.isNotBlank()
            && passwordsMatch
            && agreedToTerms
            && authState !is AuthState.Loading

    // Auto-navigate once Firebase confirms account creation
    LaunchedEffect(authState) {
        if (authState is AuthState.SignedIn) onCreateAccount()
    }

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
                isError              = confirmPassword.isNotBlank() && !passwordsMatch,
                supportingText       = {
                    if (confirmPassword.isNotBlank() && !passwordsMatch) {
                        Text("Passwords do not match",
                            color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
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

            // Show Firebase error if account creation fails
            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = {
                    authViewModel.clearError()
                    authViewModel.signUp(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled  = canSubmit
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color       = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create account")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        onCreateAccount = {},
        onBack          = {}
    )
}