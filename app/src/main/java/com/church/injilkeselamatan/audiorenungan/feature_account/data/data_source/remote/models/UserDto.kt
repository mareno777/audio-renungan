package com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val createdAt: Long?,
    val updatedAt: Long?,
    val ipAddress: String,
    val lastLogin: Long?,
    val playlist: List<String>? = null
)
