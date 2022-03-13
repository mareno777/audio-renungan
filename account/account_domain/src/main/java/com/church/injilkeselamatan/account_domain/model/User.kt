package com.church.injilkeselamatan.account_domain.model

data class User(
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
    fun toUpdateUserRequest(): UpdateUserRequest {
        return UpdateUserRequest(
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            ipAddress = ipAddress,
            model = model,
            profile = profile
        )
    }
}
