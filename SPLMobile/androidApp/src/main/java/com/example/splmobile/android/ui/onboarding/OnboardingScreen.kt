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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
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
    // List of Onboarding Pages
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
        // Create Pager
        HorizontalPager(
            modifier = Modifier
                .weight(10f),
            count = 3,
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) {
            // Set current page
            position -> ScreenTemplate(onboardingPage = pages[position])
        }
        // Add the pager to show which page we are on
        HorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f),
            pagerState = pagerState
            //activeColor = ,
            //inactiveColor =
        )
        // Add button only in last page
        btnOnboardFinish(
            modifier = Modifier
                .weight(1f),
            pagerState = pagerState,
        ){
            // Set Onboarding to completed
            onboardingViewModel.saveOnBoardingState(completed = true)
            navController.popBackStack()

            // Go to Authentication Page after finishing Onboarding
            navController.navigate(Screen.Authentication.route)
        }
    }


}

// Onboarding Content Layout
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
                .padding(horizontal = dimensionResource(R.dimen.big_spacer))
                .padding(top = dimensionResource(R.dimen.medium_spacer)),
            text = stringResource(id = onboardingPage.description),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// Skip Arrows Button
// TODO

// Finish Onboarding Button
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
           .padding(horizontal = dimensionResource(R.dimen.big_spacer)),
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
           ) {
               Text(
                   text = textResource(R.string.btnOnboardingFinish)
               )
           }
       }
   }
}