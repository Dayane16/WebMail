import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val calendarState = rememberSheetState()
    val eventos = remember {
        listOf(
            Evento(LocalDate.now(), "Reunião", "Reunião de equipe"),
            Evento(LocalDate.now().plusDays(1), "Aniversário", "Aniversário de Maria"),
            Evento(LocalDate.now().plusDays(3), "Entrega", "Entrega de relatório"),
            Evento(LocalDate.now().withDayOfMonth(15), "Evento Importante", "Evento marcado para o dia 15"),
            Evento(LocalDate.now().withDayOfMonth(18), "Conferência", "Conferência internacional"),
            Evento(LocalDate.now().withDayOfMonth(28), "Workshop", "Workshop de desenvolvimento")
        )
    }


    var eventoSelecionado by remember { mutableStateOf<Evento?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "Calendário de Eventos",
                    textAlign = TextAlign.Center, // Centraliza o texto
                    modifier = Modifier.fillMaxWidth() // Ocupa a largura máxima disponível
                )
            },
            backgroundColor = Color.White,
            navigationIcon = {
                IconButton(onClick = { navController.navigate("emails") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Box(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarView(
                    sheetState = calendarState,
                    selection = CalendarSelection.Date { date ->
                        eventoSelecionado = eventos.find { it.data == date }
                    }
                )

                // Lista de Eventos
                eventos.forEach { evento ->
                    Text(
                        text = "${evento.data.dayOfMonth}/${evento.data.monthValue} - ${evento.titulo}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { eventoSelecionado = evento }
                    )
                }
            }
        }

        eventoSelecionado?.let { evento ->
            Surface(
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Detalhes do Evento", style = MaterialTheme.typography.h5)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Título: ${evento.titulo}", style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Data: ${evento.data}", style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Descrição: ${evento.descricao}", style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { eventoSelecionado = null },
                        modifier = Modifier.wrapContentWidth(),
                    ) {
                        Text("Fechar")
                    }
                }
            }
        }
    }
}

data class Evento(val data: LocalDate, val titulo: String, val descricao: String)



