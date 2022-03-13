package com.church.injilkeselamatan.account_data.data_source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDto(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val updatedAt: Long? = null,
    val ipAddress: String,
    val lastLogin: Long = System.currentTimeMillis(),
    val model: String,
    val profile: String
)
