package com.omarkarimli.cora.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfoModel(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val gender: String = "",
    val bio: String = "",
)