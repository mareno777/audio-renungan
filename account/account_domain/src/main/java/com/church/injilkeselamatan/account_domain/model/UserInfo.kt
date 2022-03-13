package com.church.injilkeselamatan.account_domain.model

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
