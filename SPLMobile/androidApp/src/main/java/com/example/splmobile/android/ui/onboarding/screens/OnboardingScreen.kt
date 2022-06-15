package com.example.splmobile.android.ui.onboarding.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.splmobile.android.ui.onboarding.OnboardingPage
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.OnboardingViewModel
import com.google.accompanist.pager.*

// Build Onboarding Screen with Pages, Indicators and Buttons
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage.ParticipateScreen,
        OnboardingPage.CleanScreen,
        OnboardingPage.ShareScreen
    )

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier
                .weight(10f),
            count = 3,
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) {
            position -> ScreenTemplate(onboardingPage = pages[position])
        }
        HorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f),
            pagerState = pagerState
            //activeColor = ,
            //inactiveColor =
        )
        btnOnboardFinish(
            modifier = Modifier
                .weight(1f),
            pagerState = pagerState,
        ){
            onboardingViewModel.saveOnBoardingState(completed = true)
            navController.popBackStack()
            navController.navigate(Screen.Login.route)
        }
    }


}

// Onboarding Pages Layout
@Composable
fun ScreenTemplate(onboardingPage: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Icon
        Image(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.7f),
            painter = painterResource(id = onboardingPage.image),
            contentDescription = "Onboarding Icon"
        )

        // Title
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = onboardingPage.title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Description
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(top = 20.dp),
            text = stringResource(id = onboardingPage.description),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// Skip Arrows Button
// TODO

// Finish onboarding Button
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun btnOnboardFinish (
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {
   Row(
       modifier = modifier
           .padding(horizontal = 40.dp),
       verticalAlignment = Alignment.Top,
       horizontalArrangement = Arrangement.Center
   ) {
       // To show the button only in page 2
       AnimatedVisibility(
           modifier = Modifier.fillMaxWidth(),
           visible = pagerState.currentPage == 2
       ) {
           Button(
               onClick = onClick,
               colors = ButtonDefaults.buttonColors(
                   contentColor = Color.White
               )
           ) {
               Text(
                   text = "Finish" //TODO Change to @strings/
               )
           }
       }
   }
}