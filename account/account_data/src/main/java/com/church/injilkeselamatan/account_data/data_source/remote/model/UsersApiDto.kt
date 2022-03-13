package com.church.injilkeselamatan.account_data.data_source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersApiDto(
    val code: Int,
    @SerialName("data")
    val users: List<UserDto>,
    val message: String
)
