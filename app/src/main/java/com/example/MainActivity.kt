package com.example

import android.os.Bundle
import android.util.Log
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
        // Task 3: Global Exception Handler for crash diagnostics
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("BAKENYE_CRASH", "FATAL CRASH in thread: ${thread.name}", throwable)
        }

        Log.d("WORLD_ENGINE_DEBUG", "MainActivity onCreate - launching World Engine / Fishing Area slice")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BakenyeKidsTheme {
                MainAppScreen(viewModel = viewModel, initialShowWorldEngine = true)
            }
        }
    }
}
