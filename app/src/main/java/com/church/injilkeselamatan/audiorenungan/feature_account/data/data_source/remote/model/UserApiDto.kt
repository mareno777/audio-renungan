package com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserApiDto(
    val code: Int,
    @SerialName("data")
    val users: List<UserDto>,
    val message: String
)
