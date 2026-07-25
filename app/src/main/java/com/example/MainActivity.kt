package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.BakenyeViewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.BakenyeKidsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BakenyeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BakenyeKidsTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

