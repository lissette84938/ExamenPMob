package com.example.examenpmob

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.examenpmob.db.DataBase
import com.example.examenpmob.db.Entity
import com.example.examenpmob.ws.Fabrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(appVM: AppVM, formVM: FormVM) {


    val routineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val service = Fabrica.getDolaresService()
                val response = service.getDolares()
                Log.d("Respuesta de la API", response.toString())
                val jsonString = response.toString()
                formVM.dolar.value = response.serie[0].valor
                Log.d("JSON de respuesta", jsonString)
                Log.d("Valor del dolar", response.serie[0].valor.toString())
            } catch (e: Exception) {
                // Manejar el error
                Log.e("Error en la llamada API", e.toString())
            }
        }
    }

    var lugar by remember { mutableStateOf("") }
    var orden by remember { mutableStateOf("") }
    var precioAlojamiento by remember { mutableStateOf("") }
    var precioMovilizacion by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Lugar")

        TextField(
            value = lugar ,
            onValueChange = { lugar = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )

        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Orden")

        TextField(
            value = orden,
            onValueChange = { orden = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )


        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Precio Alojamiento")

        TextField(
            value = precioAlojamiento,
            onValueChange = { precioAlojamiento = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Precio Movilización")

        TextField(
            value = precioMovilizacion,
            onValueChange = { precioMovilizacion = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Comentarios")

        TextField(
            value = comentarios,
            onValueChange = { comentarios = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Button(onClick = {
            routineScope.launch(Dispatchers.IO) {
                val dao = DataBase.getInstace(context).entityDao()
                val newPlace = Entity(
                    0,
                    lugar,
                    null,
                    null,
                    null,
                    orden.toInt(),
                    BigDecimal(precioAlojamiento.toDouble()/formVM.dolar.value).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    BigDecimal(precioMovilizacion.toDouble()/formVM.dolar.value).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    comentarios
                )
                dao.insert(newPlace)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lugar agregado", Toast.LENGTH_SHORT).show()
                }

                lugar = ""
                orden = ""
                precioAlojamiento = ""
                precioMovilizacion = ""
                comentarios = ""

            }
        }) {
            Text(text = "Guardar")
        }
        Spacer(modifier =   Modifier.padding(16.dp))
        Button(onClick = { appVM.currentScreen.value = Screen.Form }) {
            Text(text = "Volver")
        }
    }
}
