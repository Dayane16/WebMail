package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        // Ao clicar no ícone de calendário, navegar para a tela de calendário
                        navController.navigate("calendarScreen")
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendário")
                }

                IconButton(
                    onClick = {
                        // Ao clicar no ícone de casa, voltar para todos os emails
                        emailsState.value = allEmailsState.value
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Voltar para Todos os E-mails")
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
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Archived,
        BottomNavItem.Favorite,
        BottomNavItem.Deleted
    )
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Red,
        modifier = Modifier.shadow(elevation = 10.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp) // Ajuste o tamanho conforme necessário
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                alwaysShowLabel = true,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { startDestination ->
                            popUpTo(startDestination) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Red,
                unselectedContentColor = Color.Gray
            )
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: Int, val route: String) {
    object Home : BottomNavItem("Home", R.drawable.home, "home")
    object Archived : BottomNavItem("Archived", R.drawable.folder_full, "archived")
    object Favorite : BottomNavItem("Favorite", R.drawable.star_full_com, "favorite")
    object Deleted : BottomNavItem("Deleted", R.drawable.delete, "deleted")
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
