package com.church.injilkeselamatan.audiorenungan.experiment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BackdropScaffoldScreen() {
    BackdropScaffoldExample()
}

@ExperimentalMaterialApi
@Composable
fun BackdropScaffoldExample() {
    val scope = rememberCoroutineScope()
    val selection = remember { mutableStateOf(1) }
    val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
//    LaunchedEffect(scaffoldState) {
//        scaffoldState.reveal()
//    }
    BackdropScaffold(
        scaffoldState = scaffoldState,
        appBar = {
            TopAppBar(
                title = { Text("Backdrop scaffold") },
                navigationIcon = {
                    if (scaffoldState.isConcealed) {
                        IconButton(onClick = { scope.launch { scaffoldState.reveal() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    } else {
                        IconButton(onClick = { scope.launch { scaffoldState.conceal() } }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                actions = {
                    var clickCount by remember { mutableStateOf(0) }
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.snackbarHostState
                                    .showSnackbar("Snackbar #${++clickCount}")
                            }
                        }
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                },
                elevation = 0.dp,
                backgroundColor = Color.Transparent
            )
        },
        backLayerContent = {
            LazyColumn {
                items(5) {
                    ListItem(
                        Modifier.clickable {
                            selection.value = it
                            scope.launch { scaffoldState.conceal() }
                        },
                        text = { Text("Select $it") }
                    )
                }
            }
        },
        frontLayerContent = {
            LazyColumn {
                item {
                    Text(
                        "Selection: ${selection.value}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(50) {
                    ListItem(
                        text = { Text("Item $it") },
                        icon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    )
}

