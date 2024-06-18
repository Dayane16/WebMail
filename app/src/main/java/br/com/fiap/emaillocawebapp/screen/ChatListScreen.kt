package br.com.fiap.emaillocawebapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.ui.theme.*

data class Conversation(val id: String, val name: String, val lastMessage: String)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatListScreen(navController: NavController) {
    val conversations = remember {
        listOf(
            Conversation("1", "Alice", "Oi! Como vai?"),
            Conversation("2", "Bob", "Vamos sair para Almoçar"),
            Conversation("3", "Charlie", "Você finalizou aquele projeto?")
        )
    }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Chat")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            tint = Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            tint = Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                backgroundColor = White,
                contentColor = Black
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(conversations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.lastMessage.contains(searchQuery, ignoreCase = true)
            }) { conversation ->
                Column {
                    ConversationItem(conversation, navController)
                    Divider(color = Gray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("chat/${conversation.id}/${conversation.name}/${conversation.lastMessage}")
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Red80, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = conversation.name, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = conversation.lastMessage, style = MaterialTheme.typography.body2)
            }
        }
    }
}
