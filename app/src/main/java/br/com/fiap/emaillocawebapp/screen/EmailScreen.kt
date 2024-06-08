package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EmailsScreen(navController: NavController, emailDao: EmailDao) {
    var query by remember { mutableStateOf("") }
    val emailsState = remember { mutableStateOf<List<EmailEntity>>(emptyList()) }
    val allEmailsState = remember { mutableStateOf<List<EmailEntity>>(emptyList()) }

    // Função para pesquisar e-mails
    fun searchEmails() {
        // Chamada assíncrona para searchEmails no EmailDao
        CoroutineScope(Dispatchers.IO).launch {
            val emails = emailDao.searchEmails(query)
            withContext(Dispatchers.Main) {
                emailsState.value = emails
            }
        }
    }

    // Função para deletar um email
    fun deleteEmail(email: EmailEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            emailDao.deleteEmail(email)
            // Remove o e-mail da lista allEmailsState após a exclusão
            allEmailsState.value = allEmailsState.value.filter { it.id != email.id }
            // Atualiza a lista após deletar o email
            searchEmails()
        }
    }

    // Função para carregar todos os emails
    fun loadAllEmails() {
        CoroutineScope(Dispatchers.IO).launch {
            val emails = emailDao.getAllEmails()
            withContext(Dispatchers.Main) {
                allEmailsState.value = emails
                emailsState.value = emails // Atualiza os emails visíveis
            }
        }
    }

    LaunchedEffect(true) {
        // Chamada inicial para listar todos os e-mails
        loadAllEmails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = {
                    IconButton(onClick = {
                        // Ao clicar na lupa, realizar a pesquisa
                        searchEmails()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // Ao pressionar Enter, realizar a pesquisa
                    searchEmails()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            IconButton(
                onClick = {
                    // Ao clicar no ícone de casa, voltar para todos os emails
                    emailsState.value = allEmailsState.value
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = "Voltar para Todos os E-mails")
            }

            IconButton(
                onClick = {
                    // Ao clicar no ícone de calendário, navegar para a tela de calendário
                    navController.navigate("calendarScreen")
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendário")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(emailsState.value) { email ->
                Column {
                    EmailListItem(
                        email = email,
                        onItemClick = { navController.navigate("emailDetail/${email.id}") },
                        onDeleteClick = {
                            // Ao clicar na lixeira, deletar o email
                            deleteEmail(email)
                        }
                    )
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("composeEmail") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Escrever Novo E-mail")
        }
    }
}

@Composable
fun StarIcon(isFavorite: Boolean, onClick: () -> Unit) {
    val starIcon = if (isFavorite) {
        painterResource(id = R.drawable.star)
    } else {
        painterResource(id = R.drawable.star_full_com)
    }

    Image(
        painter = starIcon,
        contentDescription = "Favorite",
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .padding(end = 16.dp)
    )
}


@Composable
fun EmailListItem(email: EmailEntity, onItemClick: () -> Unit, onDeleteClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(text = "De: ${email.sender}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Assunto: ${email.subject}")
        }

        StarIcon(isFavorite = isFavorite) {
            isFavorite = !isFavorite // Toggle favorite status
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
