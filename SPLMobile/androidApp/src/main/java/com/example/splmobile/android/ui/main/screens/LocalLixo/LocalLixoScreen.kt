package com.example.splmobile.android.ui.main.screens.LocalLixo

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.splmobile.android.R
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LocalLixoScreen(navController: NavHostController, lixeiraID: Long,localLixoViewModel: LocalLixoViewModel) {
    var lixeira by remember { mutableStateOf(LocalLixo(0,"aa",0,"1","1","estado",false,"","")) }

    lixeira = runBlocking {
        localLixoViewModel.getLocalLixoInfo(lixeiraID)!!
    }
    LocalLixoInfo(lixeira,navController,localLixoViewModel)
}


@Composable
fun LocalLixoInfo(localLixo: LocalLixo,navController:NavHostController,localLixoViewModel:LocalLixoViewModel){
    var expanded by remember { mutableStateOf(false) }
    val updateLocalLixo = remember { mutableStateOf(false) }

    val items = listOf("Muito Sujo", "Pouco sujo", "Limpo")

    //val disabledValue = "B"
    Log.d("locallixoscreen",localLixo.estado)
    Log.d("locallixoscreen", items.indexOf(localLixo.estado).toString())

    var selectedIndex = remember { mutableStateOf(  items.indexOf(localLixo.estado)) }
    var name = remember { mutableStateOf(items[selectedIndex.value]) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center)
    ) {

        Text(
            text = "localLixo #${localLixo.id}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
        Text(
            text = "Aprovada?  ${localLixo.aprovado}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        when(items.indexOf(localLixo.estado)){
            3 -> print("not loaded")
            else -> {
                Log.d("locallixo","im here")
               // name.value = items[items.indexOf(localLixo.estado)]
                Log.d("locallixo","im here2")

                Log.d("localixo", items.indexOf(localLixo.estado).toString())

                Log.d("localixo", selectedIndex.toString())

                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                    Text(text= name.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expanded = true })
                            .background(
                                Color.Gray
                            ),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                        )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.Magenta
                            )
                    ) {
                        items.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex.value = index

                                expanded = false

                                if(selectedIndex.value!= items.indexOf(localLixo.estado)){
                                    updateLocalLixo.value = true
                                    Log.d("locallixo","im here3")
                                    Log.d("locallixo","${items[index]}")
                                    name.value = items[index]
                                }else{
                                    updateLocalLixo.value = false
                                }

                            }) {

                                Text(  textAlign = TextAlign.Center,text = s)
                            }
                        }
                    }
                }
            }
        }


    }
    Button(

        onClick = {
            navController.popBackStack()
        }

    ) {
        Text(text = "Voltar")
    }
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Button(
            onClick = {
                //localLixoViewModel.updateLocalLixoEstado(items[selectedIndex.value])
            },
            enabled = updateLocalLixo.value,


            ) {
            Text("Atualizar estado do Local de Lixo")
        }
    }


}