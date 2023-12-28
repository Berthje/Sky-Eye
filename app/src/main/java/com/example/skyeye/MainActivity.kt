package com.example.skyeye

import android.os.Bundle
import android.view.Gravity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.skyeye.ui.theme.SkyEyeTheme
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyEyeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Drawer()
                }
            }
        }
    }
}

@Composable
fun Drawer() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(
        Pair(R.drawable.weather, "Check the weather"),
        Pair(R.drawable.camera, "Search airplanes with AR"),
        Pair(R.drawable.airplane, "See all aircraft types"),
        Pair(R.drawable.runway, "See all airports"),
        Pair(R.drawable.settings, "Settings")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = { DrawerContent(drawerState, scope, items) },
        content = { Homescreen(drawerState, scope) }
    )
}

@Composable
private fun DrawerContent(drawerState: DrawerState, scope: CoroutineScope, items: List<Pair<Int, String>>) {
    ModalDrawerSheet(
        drawerShape = RectangleShape,
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
    ) {
        DrawerHeader(drawerState, scope)
        DrawerItems(items, drawerState, scope)
    }
}

@Composable
private fun DrawerHeader(drawerState: DrawerState, scope: CoroutineScope) {
    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { scope.launch { drawerState.close() } }) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /*TODO: send to register page*/ }) {
                Text(text = "Register", fontSize = 22.sp)
            }
            Text(text = "or", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 22.sp)
            TextButton(onClick = { /*TODO: send to log in page*/ }) {
                Text(text = "Log in", fontSize = 22.sp)
            }
        }
    }
    Divider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 5.dp)
    )
    Spacer(Modifier.height(18.dp))
}

@Composable
private fun DrawerItems(items: List<Pair<Int, String>>, drawerState: DrawerState, scope: CoroutineScope) {
    items.forEach { item ->
        NavigationDrawerItem(
            icon = { Icon(painterResource(item.first), tint = MaterialTheme.colorScheme.primary, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text(item.second, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp)) },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
            },
            modifier = if (item.second == "Settings") {
                Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .padding(top = 380.dp)
            } else {
                Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            }
        )
    }
}

@Composable
fun Homescreen(drawerState: DrawerState, scope: CoroutineScope) {
    Scaffold(
        topBar = {
            TopBar(drawerState, scope)
        },
        bottomBar = {
            BottomAppBar()
        }
    ) { paddingValues ->
        // Use the contentPadding parameter to apply padding to the content
        MapView(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun TopBar(drawerState: DrawerState, scope: CoroutineScope) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(Icons.Rounded.Menu, contentDescription = "menu", modifier = Modifier.size(32.dp))
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Rounded.AccountCircle, contentDescription = "avatar", modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun BottomAppBar() {
    var selectedItem by remember { mutableIntStateOf(-1) }
    val items = listOf(
        Pair(R.drawable.settings, "Settings"),
        Pair(R.drawable.weather, "Weather"),
        Pair(R.drawable.camera, "Camera"),
        Pair(R.drawable.map, "Map Type"),
        Pair(R.drawable.filter, "Filters")
    )
    NavigationBar {
        items.forEachIndexed { index, (icon, label) ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = icon), contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@Composable
fun MapView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Mapbox.getInstance(context)
            val mapView = com.mapbox.mapboxsdk.maps.MapView(context)
            val styleUrl = "https://api.maptiler.com/maps/basic-v2/style.json?key=OZkqnFxcrUbHDpJQ5a3K";
            mapView.onCreate(null)
            mapView.getMapAsync { map ->
                // Set the style after mapView was loaded
                map.setStyle(styleUrl) {
                    map.uiSettings.setAttributionMargins(15, 0, 0, 15)
                    map.uiSettings.compassGravity = Gravity.BOTTOM or Gravity.START
                    map.uiSettings.setCompassMargins(40, 0, 0, 40)
                    map.uiSettings.isAttributionEnabled = false
                    map.uiSettings.isLogoEnabled = false
                    map.uiSettings.setCompassFadeFacingNorth(false)
                    // Set the map view center
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(50.5, 4.47))
                        .zoom(3.5)
                        .bearing(2.0)
                        .build()
                }
            }
            mapView
        }
    )
}

