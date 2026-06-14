package com.example.foursquare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foursquare.ui.navigation.FourSquareNavigation
import com.example.foursquare.ui.theme.FourSquareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FourSquareTheme {
                FourSquareNavigation()}}}}
