package com.example.splmobile.android.ui.main.screens
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.database.Lixeira
import com.example.splmobile.models.LixeiraViewModel
import com.example.splmobile.models.LixeiraViewState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun MapScreen(viewModel: LixeiraViewModel,
              log: Logger
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareDogsFlow = remember(viewModel.lixeiraState, lifecycleOwner) {
        viewModel.lixeiraState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val lixeirasState by lifecycleAwareDogsFlow.collectAsState(viewModel.lixeiraState.value)

   MapScreenContent(
        lixeirasState = lixeirasState,
        onRefresh = { viewModel.refreshLixeiras() },
        onSuccess = { data -> log.v { "View updating with ${data.size} lixeiras" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
    )


}


@Composable
fun MapScreenContent(
    lixeirasState: LixeiraViewState,
    onRefresh: () -> Unit = {},
    onSuccess: (List<Lixeira>) -> Unit = {},
    onError: (String) -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = lixeirasState.isLoading),
            onRefresh = onRefresh
        ) {
            if (lixeirasState.isEmpty) {
                Empty()
            }
            val lixeiras = lixeirasState.lixeiras
            if (lixeiras != null) {
                LaunchedEffect(lixeiras) {
                    onSuccess(lixeiras)
                }
                Success(successData = lixeiras)
            }
            val error = lixeirasState.error
            if (error != null) {
                LaunchedEffect(error) {
                    onError(error)
                }
                Error(error)
            }
        }
    }
}

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.common_google_play_services_enable_button))
    }
}

@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = error)
    }
}

@Composable
fun Success(
    successData: List<Lixeira>,
) {
    DogList(lixeiras = successData)
}

@Composable
fun DogList(lixeiras: List<Lixeira>) {
    LazyColumn {
        items(lixeiras) { lixeira ->
            DogRow(lixeira) {

            }
            Divider()
        }
    }
}

@Composable
fun DogRow(breed: Lixeira, onClick: (Lixeira) -> Unit) {
    Row(
        Modifier
            .clickable { onClick(breed) }
            .padding(10.dp)
    ) {
        Text(breed.nome, Modifier.weight(1F))

    }
}


@Preview
@Composable
fun MainScreenContentPreview_Success() {
    MapScreenContent(
        lixeirasState = LixeiraViewState(
            lixeiras = listOf(

                Lixeira(1, "australian", 2,"-9.0","40.0","aaa",true,""),
                Lixeira(2, "austraalian", 2,"-9.0","40.0","aaa",true,"")
            )
        )
    )
}






