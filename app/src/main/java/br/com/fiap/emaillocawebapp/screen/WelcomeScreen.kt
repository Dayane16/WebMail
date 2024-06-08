package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import br.com.fiap.emaillocawebapp.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import br.com.fiap.emaillocawebapp.R

@Composable
fun WelcomeScreen(onLogin: (String, String) -> Unit) {
        val gradientColors = listOf(
                RedGrad,
                White
        )

        val gradientBrush = createGradientBrush(colors = gradientColors)

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush)
                        .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // Inserir imagem no topo
                Image(
                        painter = painterResource(id = R.drawable.webmaillogo),
                        contentDescription = "Welcome Image",
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)  // Ajuste conforme necessário
                )
                Text(text = "WebMail", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                                .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                                .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),

                )
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Start
                ) {
                        Text(
                                text = "Esqueceu sua senha?",
                                color = Color.Blue,
                                modifier = Modifier.clickable {
                                        // Ação para recuperar senha, por exemplo, navegação para outra tela
                                }
                        )

               }
                Button(
                        onClick = { onLogin(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = Red80,
                                contentColor = White
                        )

                ) {
                        Text("Login")
                }



        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
        WelcomeScreen { email, password ->

        }
}
