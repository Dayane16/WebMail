package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailEntity

@Composable
fun EmailDetailScreen(emailDao: EmailDao, navController: NavController, emailId: Long) {
    val emailState = remember { mutableStateOf<EmailEntity?>(null) }

    LaunchedEffect(true) {
        emailState.value = emailDao.getEmailById(emailId)
    }

    val email = emailState.value

    if (email != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "De: ${email.sender}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Assunto: ${email.subject}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Conteúdo: ${email.content}")

            // Botão para voltar
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        }
    } else {
        Text(text = "Email não encontrado")
    }
}

