package com.example.examenpmob

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Create
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.examenpmob.db.DataBase
import com.example.examenpmob.db.Entity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen {
    Form,
    Camara,
    Maps,
    AddPlace,
    ModifyPlace
}
class AppVM: ViewModel() {
    val currentScreen = mutableStateOf(Screen.Form)
    val onCameraPermissionOk:() -> Unit = {}
    var locationPermissionOk:() -> Unit = {}
}

class FormVM: ViewModel() {
    val id = mutableStateOf(0)
    val placeVisited = mutableStateOf("")
    val photo = mutableStateOf<Bitmap?>(null)
    val lat = mutableStateOf(0.0);
    val lon = mutableStateOf(0.0);
    val order = mutableStateOf(0)
    val price = mutableStateOf(0.0)
    val movePrice = mutableStateOf(0.0)
    val comments = mutableStateOf("")
    val dolar = mutableStateOf(0.0)


class MainActivity() : ComponentActivity(), Parcelable {


    private val appVM = AppVM()
    private val camaraVm = AppVM()

    private lateinit var cameraController: LifecycleCameraController

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        appVM.locationPermissionOk()
        if(it[android.Manifest.permission.CAMERA] == true) {
            camaraVm.onCameraPermissionOk()
        }
    }

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        setContent {
            AppUI(permissionLauncher = permissionLauncher, cameraController = cameraController, appVM = appVM, formVM = FormVM())
        }
    }


@Composable
fun AppUI(
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    cameraController: LifecycleCameraController,
    appVM: AppVM,
    formVM: FormVM
) {

    when(appVM.currentScreen.value) {
        Screen.Form -> {
            listaPlacesUI(appVM = appVM, formVM = formVM)
        }
        Screen.Camara -> {
            CamaraUI(permissionLauncher = permissionLauncher, cameraController = cameraController, appVM = appVM, formVM = formVM)
        }
        Screen.Maps -> {
            MapsUI(appVM = appVM, formVM = formVM, permissionLauncher = permissionLauncher)
        }
        Screen.AddPlace -> {
            Formulario(formVM = formVM , appVM = appVM)
        }
        Screen.ModifyPlace -> {
            ModificarFormulario(formVM = formVM, appVM = appVM)
        }
    }


}

@Composable
fun listaPlacesUI(appVM: AppVM,
                  formVM: FormVM,
) {
    val (places, setplaces) = remember { mutableStateOf(emptyList<Entity>()) }
    val context = LocalContext.current
    val routineScope = rememberCoroutineScope()

    LaunchedEffect(places) {
        withContext(Dispatchers.IO) {
            val dao = DataBase.getInstace(context).entityDao()
            setplaces(dao.findAll())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(places) { place ->
            PlaceItem(place, appVM = appVM,formVM) {
                setplaces(emptyList<Entity>())
            }

        }

    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment  = Alignment.BottomEnd
    ) {
        Button(onClick = { appVM.currentScreen.value = Screen.AddPlace }) {
            Text(text = "Agregar Lugar")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment  = Alignment.BottomStart
    ) {
        Button(onClick = {routineScope.launch(Dispatchers.IO) {
            val dao = DataBase.getInstace(context).entityDao()
            dao.deleteAll()
            setplaces(emptyList())
        }}) {
            Text(text = "Eliminar todo")
        }
    }

}


    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    @Composable
fun PlaceItem(place: Entity, appVM: AppVM, formVM: FormVM, onSave:() -> Unit = {}) {
    val routineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        place.imgRef?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "imagen del lugar",
                modifier = Modifier
                    .size(150.dp)
                    .padding(end = 16.dp)
                    .clickable { appVM.currentScreen.value = Screen.Camara }
            )
        } ?: Image(
            painter = painterResource(id = R.drawable.icono),
            contentDescription = "imagen del lugar",
            modifier = Modifier
                .size(150.dp)
                .padding(end = 16.dp)
                .clickable {
                    formVM.id.value = place.uid
                    appVM.currentScreen.value = Screen.Camara
                }
        )
        Column(
            verticalArrangement = Arrangement.Bottom,

            ) {
            Text(text = place.place)
            Text(text = "Precio x Noche: $" + place.price.toString() + " USD")
            Text(text = "Transporte: $" + place.movePrice.toString() + " USD")
            Text(text = "Lati: " + place.latitud.toString() + " Long: " + place.longitud.toString())
            Row {
                Icon(Icons.TwoTone.Delete, contentDescription = "Delete", Modifier.clickable {
                    routineScope.launch(Dispatchers.IO) {
                        val dao = DataBase.getInstace(context).entityDao()
                        dao.delete(place)
                        onSave()
                    }
                })
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.TwoTone.Create, contentDescription = "Modify", Modifier.clickable {
                    formVM.id.value = place.uid
                    appVM.currentScreen.value = Screen.ModifyPlace
                }
                )
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.TwoTone.Place, contentDescription = "Location", Modifier.clickable {
                    formVM.id.value = place.uid
                    appVM.currentScreen.value = Screen.Maps
                })
            }

        }
    }}
