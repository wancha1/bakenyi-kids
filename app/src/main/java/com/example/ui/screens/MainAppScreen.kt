package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.BakenyeViewModel
import com.example.ui.sanctuary.LivingSanctuaryScreen

@Composable
fun MainAppScreen(
    viewModel: BakenyeViewModel,
    initialShowWorldEngine: Boolean = true
) {
    // The entire app IS the Living Sanctuary. No Duolingo headers, bottom nav bars, XP or quizzes.
    LivingSanctuaryScreen(
        viewModel = viewModel,
        modifier = Modifier
    )
}
