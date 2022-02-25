package com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.local.model

data class UserInfo(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val createdAt: Long?,
    val updatedAt: Long?,
    val ipAddress: String,
    val lastLogin: Long?,
    val model: String,
    val profile: String,
)
