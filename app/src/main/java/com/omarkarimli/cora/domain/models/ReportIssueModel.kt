package com.omarkarimli.cora.domain.models

data class ReportIssueModel(
    val idToken: String = "",
    val personalInfo: PersonalInfoModel = PersonalInfoModel(),
    val description: String = ""
)