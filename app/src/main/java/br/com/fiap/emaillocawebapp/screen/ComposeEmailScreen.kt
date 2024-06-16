package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailEntity
import br.com.fiap.emaillocawebapp.ui.theme.Red20
import br.com.fiap.emaillocawebapp.ui.theme.Red80
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ComposeEmailScreen(navController: NavController, emailDao: EmailDao) {
    var sender by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { ComposeEmailTopBar(navController, onSendClick = {
            val newEmail = EmailEntity(
                sender = sender,
                subject = subject,
                content = content
            )
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    emailDao.insertEmail(newEmail)
                }
                navController.popBackStack()
            }
        }) },
        bottomBar = { ComposeEmailBottomBar() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = sender,
                onValueChange = { sender = it },
                label = { Text("De:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
                singleLine = true
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            TextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Assunto:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
                singleLine = true
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Conteúdo:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}
@Composable
fun ComposeEmailTopBar(navController: NavController, onSendClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("") },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = { navController.navigate("emails") }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onSendClick) {
                Icon(Icons.Filled.Send, contentDescription = "Send Email")
            }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = { /* Handle option 1 */ }) {
                    Text("Programar Envio")
                }
                DropdownMenuItem(onClick = { /* Handle option 2 */ }) {
                    Text("Adicionar aos contatos")
                }
                DropdownMenuItem(onClick = { /* Handle option 3 */ }) {
                    Text("Salvar Rascunho")
                }
            }
        }
    )
}

@Composable
fun ComposeEmailBottomBar() {
    BottomAppBar(
        backgroundColor = Red20,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Ação para anexar */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.clip),
                        contentDescription = "Anexar Arquivo",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Ação para modificar e personalizar o texto */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.letter_a),
                        contentDescription = "Personalização de texto",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Ação para editar com IA */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_ia),
                        contentDescription = "Editar com IA",
                        tint = Red80,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Ação para mudar tamanho do texto */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.size_text),
                        contentDescription = "Mudar tamanho do texto",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Ação para mudar cor do texto */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.text_color),
                        contentDescription = "Mudar cor",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}