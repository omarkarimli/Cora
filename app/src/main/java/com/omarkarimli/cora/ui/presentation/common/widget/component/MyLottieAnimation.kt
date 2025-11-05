package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun MyLottieAnimation(
    modifier: Modifier = Modifier,
    resId: Int,
    loop: Boolean = false
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(resId)
    )

    LottieAnimation(
        composition = composition,
        modifier = modifier,
        iterations =
            if (loop) LottieConstants.IterateForever
            else 1
    )
}