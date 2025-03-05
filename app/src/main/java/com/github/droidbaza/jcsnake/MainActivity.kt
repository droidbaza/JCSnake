package com.github.droidbaza.jcsnake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.droidbaza.jcsnake.ui.SnakeGameRoute
import com.github.droidbaza.jcsnake.ui.SnakeViewModel
import com.github.droidbaza.jcsnake.ui.theme.JCSnakeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = SnakeViewModel()
        setContent {
            JCSnakeTheme {
                SnakeGameRoute(vm)
            }
        }
    }
}