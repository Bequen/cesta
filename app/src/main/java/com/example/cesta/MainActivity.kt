package com.example.cesta

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cesta.data.*
import com.example.cesta.repositories.StopRepository
import com.example.cesta.ui.theme.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.min
import kotlin.time.Duration

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database by lazy { AppDb.getInstance(this) }
        val stopRepo by lazy { StopRepository(database, CestaRemoteDataSource()) }

        val model = StopViewModel(stopRepo)
        model.fetchStopFeatures("")

        val tripModel = TripViewModel(1, stopRepo)

        setContent {
            val navController = rememberNavController()

            CestaTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Darker
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { Home({ tripId: Long -> navController.navigate("trip/${tripId}") }, model) }
                        composable("trip/{tripId}", arguments = listOf(navArgument("tripId") { type = NavType.LongType })) { it -> Trip(tripModel, it.arguments?.getLong("tripId")) }
                    }
                }
            }
        }
    }
}


@Composable
fun Trip(model: TripViewModel, tripId: Long?) {
    model.fetch(tripId!!)

    if(model.uiState.trip != null) {
        Column(Modifier.padding(15.dp).fillMaxWidth()) {
            Text("Destination", style = labelStyle)
            Text(model.uiState.trip!!.trip.headsign, fontSize = 38.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(10.dp))

            Text("Stations", style = labelStyle)
            Box(Modifier
                .clip(RoundedCornerShape(5.dp))
                .border(Border, shape = RoundedCornerShape(5.dp))
                .background(Dark)
                .fillMaxWidth()) {
                LazyColumn {
                    items(model.uiState.trip!!.departures) {
                        Row(Modifier.padding(25.dp)) {
                            Text("${it.departure.hour}:${it.departure.minute}", fontSize = 28.sp, fontWeight = FontWeight.SemiBold);
                            Spacer(Modifier.weight(1f))
                            Text(it.stop!!.longName, textAlign = TextAlign.Right, fontSize = 28.sp)
                        }
                        Divider(color = BorderColor, thickness = Border.width)
                    }
                }
            }
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(onNavigateToTrip: (Long) -> Unit, model: StopViewModel) {
    Column(Modifier.padding(15.dp)) {
        StopList(onNavigateToTrip, model, model.uiState.stops)
    }
}

@Composable
fun SimpleDeparture(onNavigateToTrip: (Long) -> Unit, departure: DepartureView) {
    val remaining = remember { (departure.departure.time - Clock.System.now()).inWholeMinutes }
    if (remaining > 0) {
        Box(Modifier.clickable { onNavigateToTrip(departure.departure.tripId) }) {
            Row {
                Column {
                    Text(
                        departure.route.shortName,
                        color = Dark,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color(departure.route.color.toInt()),
                                    cornerRadius = CornerRadius(10.dp.toPx())
                                )
                            }
                            .defaultMinSize(24.dp, 0.dp)
                            .padding(4.dp, 0.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Row {
                        val col = remember { Color(departure.route.color.toInt()) }
                        Text(
                            departure.route.headsign,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = col
                        )
                    }
                    Row {


                        Text("in", fontSize = 16.sp, modifier = Modifier.padding(2.dp, 5.dp))
                        Text(
                            remaining.toString(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 45.sp
                        )
                        Text(
                            "min", /*style = LocalTextStyle.current.merge(
                        TextStyle(
                            lineHeight = 2.5.em,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        )
                    ), */fontSize = 16.sp, modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(2.dp, 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Stop(onNavigateToTrip: (Long) -> Unit, stop: StopInfoUiState) {
    Column(modifier = Modifier.padding(25.dp)) {
        Row() {
            Canvas(modifier = Modifier.size(18.dp)) {
                drawCircle(
                    color = Red,
                    center = Offset(size.width / 2, size.height / 2 + 18f),
                    radius = size.minDimension / 2,
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(stop.stop.longName, fontWeight = FontWeight.Medium, fontSize = 28.sp)
        }
        Spacer(Modifier.height(20.dp))

        LazyRow {
            items(count = min(3, stop.departures.size), key = {stop.departures[it].departure.departureId}, itemContent = { item ->
                SimpleDeparture(onNavigateToTrip, stop.departures[item])
                Spacer(Modifier.width(20.dp))
            })
        }
    }
    Divider(color = BorderColor, thickness = Border.width)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StopList(onNavigateToTrip: (Long) -> Unit, model: StopViewModel, stops: List<StopInfoUiState>) {
    Column {
        var text by remember { mutableStateOf("") }

        TextField(
            value = text,
            onValueChange = {
                text = it
                model.fetchStopFeatures(like = it)
            },
            label = { Text("Filter") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Dark, cursorColor = Foreground),
        )

        Text(
            "nearest stops", modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), fontSize = 20.sp, textAlign = TextAlign.Center, color = LightText
        )
        Box(
            Modifier
                .clip(RoundedCornerShape(5.dp))
                .border(Border, shape = RoundedCornerShape(5.dp))
                .background(Dark)
        ) {

            LazyColumn {
                items(stops) { item ->
                    //if(item.departures.size > 0)
                        Stop(onNavigateToTrip, item)
                }
            }
        }
    }
}

@Composable
fun StopInfo(state: StopInfoViewModel) {
    state.uiState.stop?.let { data ->
        Text(data.longName)
        LazyColumn {
            items(state.uiState.departures) { item ->
                Text(item.departure.time.toString())
            }
        }
    }
}

@Composable
fun Departures() {

}

@Composable
fun Greeting(name: String, state: StopViewModel) {
    Text(state.uiState.stops.size.toString())
}

/* @Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CestaTheme {
        Greeting("Android")
    }
}*/