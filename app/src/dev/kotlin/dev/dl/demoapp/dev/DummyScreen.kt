package dev.dl.demoapp.dev

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DummyScreen(
    title: String,
    navigations: List<Pair<String, () -> Unit>> = emptyList(),
) {
    // TODO This is a dummy screen for placeholder, and in the dev source set. It won't be available in qa and release builds, try to use it will cause compile error.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        navigations.forEach {
            Button(onClick = it.second) {
                Text(
                    text = it.first,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}