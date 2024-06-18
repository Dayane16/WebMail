package br.com.fiap.emaillocawebapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailEntity
import br.com.fiap.emaillocawebapp.ui.theme.DarkBlue
import br.com.fiap.emaillocawebapp.ui.theme.Gray
import br.com.fiap.emaillocawebapp.ui.theme.Red80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EmailsScreen(navController: NavController, emailDao: EmailDao) {
    var query by remember { mutableStateOf("") }
    val emailsState = remember { mutableStateOf<List<EmailEntity>>(emptyList()) }
    val allEmailsState = remember { mutableStateOf<List<EmailEntity>>(emptyList()) }

    fun searchEmails() {
        CoroutineScope(Dispatchers.IO).launch {
            val emails = emailDao.searchEmails(query)
            withContext(Dispatchers.Main) {
                emailsState.value = emails
            }
        }
    }


    fun deleteEmail(email: EmailEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            emailDao.deleteEmail(email)
            allEmailsState.value = allEmailsState.value.filter { it.id != email.id }
            searchEmails()
        }
    }


    fun loadAllEmails() {
        CoroutineScope(Dispatchers.IO).launch {
            val emails = emailDao.getAllEmails()
            withContext(Dispatchers.Main) {
                allEmailsState.value = emails
                emailsState.value = emails
            }
        }
    }

    LaunchedEffect(true) {
        loadAllEmails()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navController = navController,
                query = query,
                onQueryChange = { query = it },
                onSearch = { searchEmails() }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            ExpandableFabMenu(navController = navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(emailsState.value) { email ->
                    Column {
                        EmailListItem(
                            email = email,
                            onItemClick = { navController.navigate("emailDetail/${email.id}") },
                            onDeleteClick = { deleteEmail(email) }
                        )
                        Divider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavController,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RectangleShape)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("profileSettings")
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Red80)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    trailingIcon = {
                        IconButton(onClick = onSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSearch() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        navController.navigate("calendarScreen")
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendário")
                }
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
                        modifier = Modifier.size(24.dp)
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
        painterResource(id = R.drawable.star_colorfull)
    } else {
        painterResource(id = R.drawable.star)
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
fun FolderIcon(isFolder: Boolean, onClick: () -> Unit) {
    val folderIcon = if (isFolder) {
        painterResource(id = R.drawable.folder_colorfull)
    } else {
        painterResource(id = R.drawable.folder)
    }

    Image(
        painter = folderIcon,
        contentDescription = "Folder",
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .padding(end = 16.dp)
    )
}

@Composable
fun EmailListItem(email: EmailEntity, onItemClick: () -> Unit, onDeleteClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
    var isFolder by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically
    ) {



        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(text = "De: ${email.sender}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Assunto: ${email.subject}")
        }

        // Ícone de estrela (favorito)
        StarIcon(isFavorite = isFavorite) {
            isFavorite = !isFavorite
        }

        // Ícone de pasta
        FolderIcon(isFolder = isFolder) {
            isFolder = !isFolder
        }
    }
}

@Composable
fun ExpandableFabMenu(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { expanded = !expanded },
            backgroundColor = DarkBlue
        ) {
            Icon(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Expandir Menu",
                modifier = Modifier.size(24.dp),
                tint = Red80
            )
        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .background(Gray)
                    .width(170.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    CustomMenuItemWithCircle(
                        text = "Enviar Email",
                        icon = painterResource(id = R.drawable.ic_send),
                        onClick = { navController.navigate("composeEmail") },
                        contentColor = Red80,
                        textColor = Color.Black
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    CustomMenuItemWithCircle(
                        text = "LocalChat",
                        icon = painterResource(id = R.drawable.chat),
                        onClick = { navController.navigate("chatList") },
                        contentColor = Red80,
                        textColor = Color.Black
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    CustomMenuItemWithCircle(
                        text = "Chat Bot",
                        icon = painterResource(id = R.drawable.roboweb),
                        onClick = { navController.navigate("chatBot") },
                        contentColor = Red80,
                        textColor = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun CustomMenuItemWithCircle(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    contentColor: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(DarkBlue, shape = CircleShape)
                .padding(4.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = textColor)
    }
}

