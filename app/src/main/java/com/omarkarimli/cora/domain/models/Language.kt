package com.omarkarimli.cora.domain.models

data class Language(
    val code: String,
    val displayLanguage: String
)

val appLanguages = listOf(
    Language("en", "English"),
    Language("de", "Deutsch"),
    Language("tr", "Türkçe"),
    Language("fr", "Français"),
    Language("it", "Italiano"),
    Language("es", "Español"),
    Language("ru", "Русский"),
    Language("pl", "Polski"),
    Language("pt", "Português"),
    Language("az", "Azərbaycanca"),
    Language("iw", "עִברִית"),
    Language("ar", "اللغة العربية"),
    Language("hi", "हिंदी"),
    Language("bn", "বাংলা"),
    Language("zh", "中文"),
    Language("ja", "日本語"),
    Language("ko", "한국어")
)