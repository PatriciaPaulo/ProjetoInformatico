import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.components.SearchBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState

@Composable
fun DefaultAppBar(
    title: String,
    onSearchClicked: ()-> Unit){
    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        actions = {
            IconButton(
                onClick = { onSearchClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Procurar icon",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun MapAppBar(
    title: String,
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: ()-> Unit,
    onSearchClicked:(String) ->Unit,
    onSearchTriggered: () -> Unit
){
    when(searchWidgetState){
        SearchWidgetState.CLOSED -> {
            DefaultAppBar (
                title = title,
                onSearchClicked = onSearchTriggered
            )
        }
        SearchWidgetState.OPENED->{
            SearchBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}
