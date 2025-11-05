package com.omarkarimli.cora.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.omarkarimli.cora.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppins = GoogleFont("Poppins")

val bodyFontFamily = FontFamily(
    Font(
        googleFont = poppins,
        fontProvider = provider,
    )
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = poppins,
        fontProvider = provider,
    )
)