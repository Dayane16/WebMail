package br.com.fiap.emaillocawebapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.ui.theme.*
import kotlinx.coroutines.launch

data class ChatBotMessage(
    val id: Int,
    val content: String,
    val senderId: Int,
    val timestamp: Long
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatBotScreen(
    navController: NavController,
    conversationName: String = "chatbot"
) {
    val messageList = remember { mutableStateListOf<ChatBotMessage>() }

    LaunchedEffect(Unit) {
        messageList.add(
            ChatBotMessage(
                id = 0,
                content = "Escolha como posso te ajudar!\n" +
                        "1 - Resumir Emails\n" +
                        "2 - Apagar Emails a partir de tal data\n" +
                        "3 - Outras opções",
                senderId = 2,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                modifier = Modifier.height(75.dp),
                elevation = 4.dp,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.roboweb),
                                contentDescription = "Profile",
                                modifier = Modifier.size(45.dp)
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(messageList.size) { index ->
                    val message = messageList[index]
                    ChatBotMessageBubble(
                        message = message,
                        isMyMessage = message.senderId == 1
                    )
                }
            }

            ChatBotInputField(
                lastMessage = "",
                onSendMessage = { content ->
                    val newMessage = ChatBotMessage(
                        id = messageList.size + 1,
                        content = content,
                        senderId = 1,
                        timestamp = System.currentTimeMillis()
                    )
                    messageList.add(newMessage)

                    coroutineScope.launch {
                        listState.scrollToItem(messageList.size - 1)
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBotMessageBubble(
    message: ChatBotMessage,
    isMyMessage: Boolean
) {
    val backgroundColor = if (isMyMessage) Red80 else Red40
    val contentColor = if (isMyMessage) White else Black

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = backgroundColor,
            elevation = 4.dp,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = message.content,
                    color = contentColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ChatBotInputField(
    lastMessage: String,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf(lastMessage) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(4.dp)
            .background(Red40, RoundedCornerShape(20.dp))
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = { /* Ação do ícone de emoji */ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.smile),
                contentDescription = "Emoji",
                modifier = Modifier.size(24.dp)
            )
        }

        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            placeholder = { Text(text = "Menssagem...") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                textColor = Color.Black
            )
        )

        IconButton(
            onClick = { /* Ação do ícone de clipe */ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.clip),
                contentDescription = "Attach File",
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    onSendMessage(messageText)
                    messageText = ""
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "Send",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
