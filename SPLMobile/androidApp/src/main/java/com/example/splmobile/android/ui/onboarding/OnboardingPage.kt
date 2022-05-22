package com.example.splmobile.android.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.splmobile.android.R

//Add actual values to Onboarding Pages
sealed class OnboardingPage (
    @DrawableRes
    val image: Int,
    @StringRes
    val title: Int,
    @StringRes
    val description: Int
    ) {
    object ParticipateScreen : OnboardingPage(
        image = R.drawable.ic_onboarding_participate,
        title = R.string.participateTitle,
        description = R.string.participateDescription,
    )

    object CleanScreen : OnboardingPage(
        image = R.drawable.ic_onboarding_clean,
        title = R.string.cleanTitle,
        description = R.string.cleanDescription,
    )

    object ShareScreen : OnboardingPage(
        image = R.drawable.ic_onboarding_schare,
        title = R.string.shareTitle,
        description = R.string.shareDescription,
    )
}