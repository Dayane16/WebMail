package br.com.fiap.emaillocawebapp

import CalendarScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.fiap.emaillocawebapp.dao.EmailDao
import br.com.fiap.emaillocawebapp.data.EmailDatabase
import br.com.fiap.emaillocawebapp.screen.ComposeEmailScreen
import br.com.fiap.emaillocawebapp.screen.EmailDetailScreen
import br.com.fiap.emaillocawebapp.screen.EmailsScreen
import br.com.fiap.emaillocawebapp.screen.WelcomeScreen

class MainActivity : ComponentActivity() {
    private lateinit var emailDao: EmailDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar o banco de dados e o DAO
        val emailDatabase = EmailDatabase.getDatabase(applicationContext)
        emailDao = emailDatabase.emailDao()

        setContent {
            MyApp(emailDao = emailDao)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MyApp(emailDao: EmailDao) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen { email, password ->
                    // Faça a navegação ou a ação necessária com email e password
                    navController.navigate("emails")
                }
            }
            composable("emails") {
                EmailsScreen(navController = navController, emailDao = emailDao)
            }
            composable(
                "composeEmail",
                content = { ComposeEmailScreen(navController = navController, emailDao = emailDao) }
            )
            composable(
                "calendarScreen",
                content = { CalendarScreen(navController = navController) }
            )
            composable(
                "emailDetail/{emailId}",
                arguments = listOf(navArgument("emailId") { type = NavType.LongType }),
                content = { backStackEntry ->
                    val arguments = requireNotNull(backStackEntry.arguments)
                    val emailId = arguments.getLong("emailId")
                    EmailDetailScreen(emailDao = emailDao, navController = navController, emailId = emailId)
                }
            )
        }
    }
}


