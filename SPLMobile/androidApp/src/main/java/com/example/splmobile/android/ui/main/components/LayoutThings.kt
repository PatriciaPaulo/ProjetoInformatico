package com.example.splmobile.android.ui.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HdrPlus
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.splmobile.android.R


// TODO Change File Name

@Preview
@Composable
fun test() {
    iconBoxUI("Vale Limpar", "Mira de Aire", 13.0, "22-10-2013 15h00", null)
    ListItemUI("Titulo Garbage", "cenas cenas cenas")
}



@Composable
fun iconBoxUI(name : String, location : String?, distance : Double?, details : String, iconPath : String?){
    val cornerRadius = RoundedCornerShape(dimensionResource(R.dimen.cornerRadius))

    Box(
        modifier = Modifier
            .clip(cornerRadius)
            .background(Color.White)
            .height(150.dp)
            .width(125.dp),
    ) {
        // IMG FULL BOX
        if(iconPath != null) {
            Image(
                painter = rememberAsyncImagePainter(iconPath),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
            )
        }
        Column (
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = -100f,
                        endY = 300f
                    )
                )
                .padding(
                    start = dimensionResource(R.dimen.small_spacer),
                    end = dimensionResource(R.dimen.small_spacer),
                    bottom = dimensionResource(R.dimen.textbox_margin),
                    top = 0.dp
                )
                .align(Alignment.BottomEnd)
                .fillMaxWidth(),
        ) {
            // Event/Garbage Spot Name
            Text(
                text = name,
                fontSize = dimensionResource(R.dimen.txt_medium).value.sp,
                color = MaterialTheme.colors.onPrimary
            )

            if(location != null) {
                Text(
                    text = location,
                    fontSize = dimensionResource(R.dimen.description).value.sp,
                    color = MaterialTheme.colors.onPrimary
                )
            }

            // Distance
            if(distance != null) {
                Text(
                    text = "a $distance km",
                    fontSize = dimensionResource(R.dimen.description).value.sp,
                    color = MaterialTheme.colors.onPrimary
                )
            }

            // Details
            // Date will be shown if box content is an event
            // Garbage quantity shown if box content is garbage spot
            Text(
                text = details,
                fontSize = dimensionResource(R.dimen.description).value.sp,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
fun ListItemUI(title : String, details : String) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = title,
                fontSize = dimensionResource(R.dimen.txt_large).value.sp,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.small_spacer))
            )
            Text(
                text = details,
                fontSize = dimensionResource(R.dimen.txt_medium).value.sp,
            )
        }

}