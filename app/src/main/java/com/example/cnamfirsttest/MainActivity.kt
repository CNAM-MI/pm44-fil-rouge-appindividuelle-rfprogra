package com.example.cnamfirsttest


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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.socket.client.IO
import kotlinx.coroutines.launch
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
            val tempssondagejour = mutableIntStateOf(tempsjour)
            val tempsheur=0
            val tempssondageheur = mutableIntStateOf(tempsheur)

            val navController = rememberNavController()


            val user = User(31,"Rick","test",null)

            val listsondages = mutableListOf<Sondage>()
            val listvotes = mutableListOf<Vote>()
            /*
            val vote1 = Vote(1,3,"lundi","hourvote.toString()")
            val vote2 = Vote(1,5,"lundi","hourvote.toString()")
            val vote3 = Vote(1,9,"jeudi","hourvote.toString()")
            listvotes.add(vote1)
            listvotes.add(vote2)
            listvotes.add(vote3)
            */
            @Composable
            fun sondageItem(sondage:Sondage){
                Surface(onClick = {navController.navigate("sondageJour/"+sondage.id.toString())} ) {
                    Column (modifier=Modifier
                        .padding(2.dp)
                        .border(width = 1.dp,
                            color = Color.Blue,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(2.dp)
                        .background(
                            color=Color.White
                        )
                    ){
                        Text(text = "sondage id "+sondage.id+" créé par "+sondage.nomCrea,
                            color=Color.Black)

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
                composable("Home"){Column (modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

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
                                mSocket.connect()
                                Log.d("socket", "Connect")
                                mSocket.emit("test emission de richard")

                            }
                            catch(_:URISyntaxException){}
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
                composable("Createsondage"){Column (modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        var text by remember { mutableStateOf("") }
                        var errorText by remember { mutableStateOf("")}
                        FilledTonalButton(
                            onClick = {
                                navController.navigate("Home")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                        ){
                            Text(
                                "Retour",
                                color = Color.White
                            )
                        }
                        Text(text = "Nom",color=Color.White)

                        OutlinedTextField(
                            value = text,
                            onValueChange = { newtext -> text = newtext },
                            label = { Text(text = "Nom",color=Color.White) }
                        )
                        Text(text = "Temps de sondage jour",color=Color.White)
                        Row{

                            Button(onClick = { tempssondagejour.intValue -= 1 }) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempssondagejour.intValue.toString(),
                                color=Color.White
                            )
                            Button(onClick = { tempssondagejour.intValue += 1 }) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        Text(text = "Temps de sondage horaire", color=Color.White)
                        Row{

                            Button(onClick = { tempssondageheur.intValue -= 1 }) {
                                Text(
                                    "-"
                                )
                            }
                            Text(
                                text = tempssondageheur.intValue.toString(),
                                color=Color.White
                            )
                            Button(onClick = { tempssondageheur.intValue += 1 }) {
                                Text(
                                    "+"
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = {
                                if(text!=""&&tempssondagejour.intValue>0&&tempssondageheur.intValue>0){
                                    val date = Date()

                                    listsondages.add(Sondage(text,
                                        tempssondagejour.intValue,
                                        tempssondageheur.intValue,
                                        listsondages.size+1,
                                        user.id,
                                        null,
                                        date
                                    ))
                                    navController.navigate("Home")
                                }
                                else{
                                    errorText="Veuillez remplir corectement les champs"
                                }


                                },
                            colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                        ){
                            Text(
                                "Valider",
                                color = Color.White
                            )
                        }
                        Text(errorText,color=Color.Red)
                    }
                }

                composable("sondageJour/{idVote}"){
                    backStactEntry -> val idVote = backStactEntry.arguments?.getString("idVote")
                    val idVoteInt = idVote?.toInt()
                    val composableScope = rememberCoroutineScope()
                    var accelerationX by remember { mutableStateOf(0f) }

                    var rotation by remember { mutableStateOf(0f) }
                    val offset = Offset(0f,00f)
                    var dayvote by remember { mutableStateOf("") }
                    var hourvote = 0
                    val textMeasurer = rememberTextMeasurer()
                    var lundi by remember { mutableStateOf(0) }
                    var mardi by remember { mutableStateOf(0) }
                    var mercredi by remember { mutableStateOf(0) }
                    var jeudi by remember { mutableStateOf(0) }
                    var vendredi by remember { mutableStateOf(0) }
                    var samedi by remember { mutableStateOf(0) }
                    var dimanche by remember { mutableStateOf(0) }
                    var alreadyVoted by remember { mutableStateOf(false) }
                    var colorBtnVote by remember { mutableStateOf(Color.Blue) }

                    fun checkVotes(){
                         lundi =0
                         mardi =0
                        mercredi =0
                         jeudi =0
                        vendredi =0
                        samedi =0
                        dimanche =0
                        listvotes.forEach{
                            vote ->
                            println("userid :"+ vote.user)
                            if(vote.id.toString()==idVote){
                                when(vote.day){
                                    "lundi"->lundi+=1
                                    "mardi"->mardi+=1
                                    "mercredi"->mercredi+=1
                                    "jeudi"->jeudi+=1
                                    "vendredi"->vendredi+=1
                                    "samedi"->samedi+=1
                                    "dimanche"->dimanche+=1
                                }
                            }
                            if(vote.user==user.id&&vote.id==idVoteInt){
                                alreadyVoted=true
                                colorBtnVote=Color.Gray
                            }

                        }
                    }
                    LaunchedEffect(composableScope){
                        composableScope.launch{
                            checkVotes()
                        }
                    }

                    val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


                    val sensorEventListener = object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent) {
                            // Process sensor data
                            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                                accelerationX = event.values[0]
                                rotation = -truncate(accelerationX)*10
                                // Process accelerometer data
                                when(rotation){
                                    in -90f..-64f -> dayvote="lundi"
                                    in -64f..-38f -> dayvote="mardi"
                                    in -38f..-12f -> dayvote="mercredi"
                                    in -12f..14f -> dayvote="jeudi"
                                    in 14f..40f -> dayvote="vendredi"
                                    in 40f..66f -> dayvote="samedi"
                                    in 66f..91f -> dayvote="dimanche"
                                }
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

                        Text(text = "Vote n°$idVote, choix jour", color=Color.White)

                        Canvas(
                                modifier = Modifier
                                    .size(300.dp)
                                    .drawBehind { }
                                ) {

                            val blue = Color(0,100,255)
                            val green =Color(0,255,0)
                            val red = Color(255,0,0)



                            translate(top=300f){

                                drawArc(blue,180f,25f, true,offset )
                                drawArc(green,206f,25f, true,offset)
                                drawArc(red,232f,25f, true,offset)
                                drawArc(green,258f,25f, true,offset)
                                drawArc(blue,284f,25f, true,offset)
                                drawArc(green,310f,25f, true,offset)
                                drawArc(red,336f,25f, true,offset)

                                scale(1.2F,1.2f){
                                    when(dayvote){
                                        "lundi"->drawArc(blue,180f,25f, true,offset)
                                        "mardi"->drawArc(green,206f,25f, true,offset)
                                        "mercredi"->drawArc(red,232f,25f, true,offset)
                                        "jeudi"->drawArc(green,258f,25f, true,offset)
                                        "vendredi"->drawArc(blue,284f,25f, true,offset )
                                        "samedi"->drawArc(green,310f,25f, true,offset)
                                        "dimanche"->drawArc(red,336f,25f, true,offset )
                                    }
                                }
                                translate(top=320f,left=60f) {
                                    drawText(textMeasurer, "lundi")
                                }
                                translate(top=280f,left=-100f) {
                                    rotate(25f){
                                        drawText(textMeasurer, "mardi")
                                    }

                                }
                                translate(top=250f,left=-230f) {
                                    rotate(55f){
                                        drawText(textMeasurer, "mercredi")
                                    }

                                }
                                translate(top=50f,left=350f) {
                                    rotate(0f){
                                        drawText(textMeasurer, "jeudi")
                                    }

                                }
                                translate(top=-270f,left=600f) {
                                    rotate(-55f){
                                        drawText(textMeasurer, "vendredi")
                                    }

                                }
                                translate(top=5f,left=700f) {
                                    rotate(-30f){
                                        drawText(textMeasurer, "samedi")
                                    }

                                }
                                translate(top=320f,left=600f) {
                                    rotate(0f){
                                        drawText(textMeasurer, "dimanche")
                                    }

                                }


                            }

                        }
                        FilledTonalButton(
                            onClick = {
                                if(alreadyVoted==false){
                                    when(dayvote){
                                        "lundi"->dayvote="lundi"
                                        "mardi"->dayvote="mardi"
                                        "mercredi"->dayvote="mercredi"
                                        "jeudi"->dayvote="jeudi"
                                        "vendredi"->dayvote="vendredi"
                                        "samedi"->dayvote="samedi"
                                        "dimanche"->dayvote="dimanche"
                                    }
                                    var vote = idVoteInt?.let { Vote(it,user.id,dayvote,hourvote.toString()) }
                                    if (vote != null) {
                                        listvotes.add(vote)
                                    }
                                    checkVotes()
                                }

                            },
                            colors = ButtonDefaults.buttonColors(containerColor  = colorBtnVote)
                        )
                        {
                            Text(
                                "Voter",
                                color = Color.White
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                navController.navigate("Home")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor  = Color.Blue)
                        )
                        {
                            Text(
                                "Retour",
                                color = Color.White
                            )
                        }
                        Text("Resultats vote :", color=Color.White)
                        Text("lundi:$lundi mardi:$mardi mercredi:$mercredi jeudi:$jeudi vendredi:$vendredi samedi:$samedi dimanche:$dimanche", color=Color.White)


                    }


                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}

class Sondage(
    val nomCrea:String,
    var timeTosondageJour: Int,
    var timeTosondageHour: Int,
    val id:Int,
    val user:Int,
    var pict:Blob?,
    val dateCreation: Date
)
class User(
    val id:Int,
    var pseudo:String,
    var password:String,
    var pict: Blob?)
class Vote(
    val id:Int,
    val user:Int,
    val day:String,
    val hour:String
)
