package com.example.cnamfirsttest

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
//import java.net.Socket
import kotlin.math.truncate


class MainActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {

        val socket = Socket(
            endpoint = "//84.235.235.229:3000/",
            config = SocketOptions(
                queryParams = mapOf("token" to "MySuperToken"),
                transport = SocketOptions.Transport.WEBSOCKET
            )
        ) {
            on(SocketEvent.Connect) {
                println("connect")
            }

            on(SocketEvent.Connecting) {
                println("connecting")
            }

            on(SocketEvent.Disconnect) {
                println("disconnect")
            }

            on(SocketEvent.Error) {
                println("error $it")
            }

            on(SocketEvent.Reconnect) {
                println("reconnect")
            }

            on(SocketEvent.ReconnectAttempt) {
                println("reconnect attempt $it")
            }

            on(SocketEvent.Ping) {
                println("ping")
            }

            on(SocketEvent.Pong) {
                println("pong")
            }


        }

        super.onCreate(savedInstanceState)
        setContent {
            //variable temps pour vote
            var tempsjour=0
            var tempsvotejour = mutableIntStateOf(tempsjour)
            var tempsheur=0
            var tempsvoteheur = mutableIntStateOf(tempsheur)
            //val navController = findNavController(R.id.nav_host_fragment)
            val navController = rememberNavController()


            class Vote(val nomCrea:String,var timeToVoteJour: Int,var timeToVoteHour: Int, var id:Int){

            }
            var listVotes = mutableListOf<Vote>()
            @Composable
            fun VoteItem(vote:Vote){
                Surface(onClick = {navController.navigate("VoteJour/"+vote.id.toString())} ) {
                    Column {
                        Text(text = "Vote id "+vote.id+" créé par "+vote.nomCrea,
                            color=Color.Cyan)

                        Row {
                            Text(text = "Temps jour :"+vote.timeToVoteJour.toString())
                            Text(text = "Temps heure "+vote.timeToVoteHour.toString())
                        }
                    }
                }
            }
            @Composable
            fun ListVote(votes:List<Vote>){
                Column {
                    votes.forEach{Vote ->
                        VoteItem(Vote)
                    }
                }
            }

            // Add the graph to the NavController with `createGraph()`.
            NavHost(navController = navController, startDestination = "Home" ) {
                composable("Home"){Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                    //val context = LocalContext.current
                    ListVote(votes = listVotes )
                    FilledTonalButton(
                        onClick = {
                            navController.navigate("CreateVote")
                            //context.startActivity(Intent(context,CreateVote::class.java))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                    )
                    {
                        Text(
                            "Créer un vote",
                            color = Color.White
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            socket.connect()
                            println("test")
                            Log.d("socket","Connect?")
                            //context.startActivity(Intent(context,CreateVote::class.java))
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
                composable("CreateVote"){Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        var text by remember { mutableStateOf("") }
                        FilledTonalButton(
                            onClick = {
                                navController.navigate("Home")
                                //context.startActivity(Intent(context,CreateVote::class.java))
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
                        Text(text = "Temps de vote jour")
                        Row{

                            Button(onClick = { tempsvotejour.value= tempsvotejour.value-1}) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempsvotejour.value.toString()
                            )
                            Button(onClick = { tempsvotejour.value= tempsvotejour.value+1}) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        Text(text = "Temps de vote horaire")
                        Row{

                            Button(onClick = { tempsvoteheur.value= tempsvoteheur.value-1}) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempsvoteheur.value.toString()
                            )
                            Button(onClick = { tempsvoteheur.value= tempsvoteheur.value+1}) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = {
                                listVotes.add(Vote(text,tempsvotejour.value,tempsvoteheur.value,listVotes.size+1))
                                navController.navigate("Home")
                                //context.startActivity(Intent(context,CreateVote::class.java))
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

                composable("VoteJour/{idVote}"){
                    backStactEntry -> val idVote = backStactEntry.arguments?.getString("idVote")

                    var accelerationX by remember { mutableStateOf(0f) }

                    var rotation by remember { mutableStateOf(0f) }
                    val offset:Offset= Offset(0f,00f)

                    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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

                        Text(text = "Vote n°"+idVote+", choix jour")

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

                            var blue = Color(0,100,255)
                            var green =Color(0,255,0)
                            var red = Color(255,0,0)
                            var black = Color(0,0,0)

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
}

