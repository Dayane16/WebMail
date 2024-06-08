package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import br.com.fiap.emaillocawebapp.ui.theme.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeScreen(navigateToNext: () -> Unit) {
        val gradientColors = listOf(
                RedGrad,
                White
        )

        val gradientBrush = createGradientBrush(colors = gradientColors)

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush) // Aplicar o gradiente aqui
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(text = "Bem-vindo ao LocalWEBMail!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigateToNext() }) {
                        Text("Próximo")
                }
        }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
        WelcomeScreen(navigateToNext = {})
}
