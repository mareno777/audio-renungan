package com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model

import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.local.model.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val ipAddress: String = "ip",
    val lastLogin: Long = System.currentTimeMillis(),
    val model: String,
    val profile: String
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            createdAt = null,
            updatedAt = updatedAt,
            ipAddress = ipAddress,
            lastLogin = lastLogin,
            model = model,
            profile = profile
        )
    }
}
