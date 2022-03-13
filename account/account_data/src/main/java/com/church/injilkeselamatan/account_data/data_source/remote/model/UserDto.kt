package com.church.injilkeselamatan.account_data.data_source.remote.model

import com.church.injilkeselamatan.account_domain.model.User
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
    val model: String,
    val profile: String,
    val playlist: List<String>? = null
) {

    fun toUser(): User {
        return User(
            id,
            email,
            name,
            phoneNumber,
            createdAt,
            updatedAt,
            ipAddress,
            lastLogin,
            model,
            profile,
            playlist
        )
    }
}
