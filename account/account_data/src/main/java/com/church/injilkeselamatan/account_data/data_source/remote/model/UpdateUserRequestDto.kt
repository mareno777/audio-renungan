package com.church.injilkeselamatan.account_data.data_source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestDto(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val ipAddress: String = "ip",
    val lastLogin: Long = System.currentTimeMillis(),
    val model: String,
    val profile: String
)
