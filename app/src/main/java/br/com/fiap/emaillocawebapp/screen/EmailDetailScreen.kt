package br.com.fiap.emaillocawebapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.fiap.emaillocawebapp.R
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailEntity
import br.com.fiap.emaillocawebapp.ui.theme.Black
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val allEmailsState = mutableStateListOf<EmailEntity>()
fun deleteEmail(email: EmailEntity, emailDao: EmailDao, searchEmails: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        emailDao.deleteEmail(email)
        allEmailsState.remove(email)
        searchEmails()
    }
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EmailDetailScreen(emailDao: EmailDao, navController: NavController, emailId: Long) {

    val emailState = remember { mutableStateOf<EmailEntity?>(null) }

    LaunchedEffect(true) {
        emailState.value = emailDao.getEmailById(emailId)
    }

    val email = emailState.value

    Scaffold(
        topBar = {
            DetailEmailTopBar(navController = navController, emailDao = emailDao, email = email) {

            }
        }
    ) {

        email?.let { email ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 10.dp),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = "De: ${email.sender}",
                        style = MaterialTheme.typography.h6
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Assunto: ${email.subject}",
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = " ${email.content}",
                    style = MaterialTheme.typography.body1
                )

            }
        } ?: run {

            Text(
                text = "Email não encontrado",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun DetailEmailTopBar(
    navController: NavController,
    emailDao: EmailDao,
    email: EmailEntity?,
    searchEmails: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = {
            if (email != null) {
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Favoritar Email",
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }


                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.folder),
                        contentDescription = "Mover Email",
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }
                IconButton(onClick = {
                    deleteEmail(email, emailDao, searchEmails)
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Deletar Email",
                        modifier = Modifier.size(24.dp),
                        tint = Black
                    )
                }

            }
        }
    )
}

