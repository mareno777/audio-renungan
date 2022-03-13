package com.church.injilkeselamatan.account_domain.model

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
