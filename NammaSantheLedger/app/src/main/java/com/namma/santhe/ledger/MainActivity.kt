package com.namma.santhe.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.namma.santhe.ledger.ui.theme.NammaSantheTheme
import com.namma.santhe.ledger.ui.navigation.NammaSantheNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NammaSantheTheme {
                NammaSantheNavGraph()
            }
        }
    }
}
