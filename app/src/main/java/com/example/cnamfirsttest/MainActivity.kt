package com.example.cnamfirsttest


//import java.net.Socket

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.socket.client.IO
import java.net.URISyntaxException
import java.sql.Blob
import java.util.Date
import kotlin.math.truncate

var mSocket = IO.socket("http://84.235.235.229:3000/")
class MainActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContent {
            //variable temps pour sondage
            val tempsjour=0
            var tempssondagejour = mutableIntStateOf(tempsjour)
            val tempsheur=0
            var tempssondageheur = mutableIntStateOf(tempsheur)
            //val navController = findNavController(R.id.nav_host_fragment)
            val navController = rememberNavController()


            var user = User(31,"Rick","test",null)

            var listsondages = mutableListOf<Sondage>()
            @Composable
            fun sondageItem(sondage:Sondage){
                Surface(onClick = {navController.navigate("sondageJour/"+sondage.id.toString())} ) {
                    Column {
                        Text(text = "sondage id "+sondage.id+" créé par "+sondage.nomCrea,
                            color=Color.Cyan)

                        Row {
                            Text(text = "Temps jour :"+sondage.timeTosondageJour.toString())
                            Text(text = "Temps heure "+sondage.timeTosondageHour.toString())
                        }
                    }
                }
            }
            @Composable
            fun Listsondage(sondages:List<Sondage>){
                Column {
                    sondages.forEach{sondage ->
                        sondageItem(sondage)
                    }
                }
            }

            // Add the graph to the NavController with `createGraph()`.
            NavHost(navController = navController, startDestination = "Home" ) {
                composable("Home"){Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                    //val context = LocalContext.current
                    //var mSocket = IO.socket("http://84.235.235.229:3000/")
                    Listsondage(sondages = listsondages )
                    FilledTonalButton(
                        onClick = {
                            navController.navigate("Createsondage")
                            //context.startActivity(Intent(context,Createsondage::class.java))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                    )
                    {
                        Text(
                            "Créer un sondage",
                            color = Color.White
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            try {
                                mSocket.connect();
                                Log.d("socket", "Connect")
                                mSocket.emit("test emission de richard")
                                //context.startActivity(Intent(context,Createsondage::class.java))
                            }
                            catch( e:URISyntaxException){}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                    )
                    {
                        Text(
                            "Connection au serveur",
                            color = Color.White
                        )
                    }

                }}
                composable("Createsondage"){Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        var text by remember { mutableStateOf("") }
                        FilledTonalButton(
                            onClick = {
                                navController.navigate("Home")
                                //context.startActivity(Intent(context,Createsondage::class.java))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                        ){
                            Text(
                                "Retour",
                                color = Color.White
                            )
                        }
                        Text(text = "Nom")

                        OutlinedTextField(
                            value = text,
                            onValueChange = { newtext -> text = newtext },
                            label = { Text(text = "Nom") }
                        )
                        Text(text = "Temps de sondage jour")
                        Row{

                            Button(onClick = { tempssondagejour.value= tempssondagejour.value-1}) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempssondagejour.value.toString()
                            )
                            Button(onClick = { tempssondagejour.value= tempssondagejour.value+1}) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        Text(text = "Temps de sondage horaire")
                        Row{

                            Button(onClick = { tempssondageheur.value= tempssondageheur.value-1}) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempssondageheur.value.toString()
                            )
                            Button(onClick = { tempssondageheur.value= tempssondageheur.value+1}) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = {

                                val date = Date()

                                listsondages.add(Sondage(text,
                                    tempssondagejour.value,
                                    tempssondageheur.value,
                                    listsondages.size+1,
                                    user.id,
                                    null,
                                    date
                                ))
                                navController.navigate("Home")

                                },
                            colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                        ){
                            Text(
                                "Valider",
                                color = Color.White
                            )
                        }
                    }
                }

                composable("sondageJour/{idVote}"){
                    backStactEntry -> val idVote = backStactEntry.arguments?.getString("idVote")

                    var accelerationX by remember { mutableStateOf(0f) }

                    var rotation by remember { mutableStateOf(0f) }
                    val offset = Offset(0f,00f)

                    val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                    //val sensorRot: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                    //val sensorAcc: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

                    var sensorEventListener = object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent) {
                            // Process sensor data
                            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                                accelerationX = event.values[0]
                                rotation = -truncate(accelerationX)*10
                                // Process accelerometer data
                            }
                        }
                        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                            // Handle sensor accuracy changes
                        }
                    }
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                        ?.also{
                        sensorManager.registerListener(sensorEventListener,
                            it,
                            SensorManager.SENSOR_DELAY_UI/2)
                    }




                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){

                        Text(text = "Vote n°$idVote, choix jour")

                        Canvas(modifier = Modifier
                            .size(250.dp)
                            .drawBehind { }) {
                            val roundedPolygon = RoundedPolygon(
                                numVertices = 3,
                                radius = size.minDimension / 8,
                                centerX = size.width / 18,
                                centerY = size.height /2

                            )
                            rotate(degrees = 90f){
                                val roundedPolygonPath = roundedPolygon.toPath().asComposePath()
                                drawPath(roundedPolygonPath,Color.LightGray)
                            }
                        }
                        Canvas(
                                modifier = Modifier
                                    .size(250.dp)
                                    .drawBehind { }
                                ) {

                            val blue = Color(0,100,255)
                            val green =Color(0,255,0)
                            val red = Color(255,0,0)


                            drawArc(Color.Yellow,257f,27f, true,offset)
                            rotate(degrees = rotation){

                                drawArc(blue,180f,25f, true,offset )
                                drawArc(green,206f,25f, true,offset)
                                drawArc(red,232f,25f, true,offset)
                                drawArc(green,258f,25f, true,offset)
                                drawArc(blue,284f,25f, true,offset)
                                drawArc(green,310f,25f, true,offset)
                                drawArc(red,336f,25f, true,offset)
                            }

                        }

                    }


                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect();
    }
}

class Sondage(
    val nomCrea:String,
    var timeTosondageJour: Int,
    var timeTosondageHour: Int, val id:Int,
    val user:Int,
    var pict:Blob?,
    val dateCreation: Date
){

}
class User(val id:Int, var pseudo:String, var password:String, var pict: Blob?){
}
class Vote(val id:Int,val user:Int, val day:String, val hour:String ){

}

