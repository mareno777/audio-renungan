package com.church.injilkeselamatan.audiorenungan.feature_account.domain.use_case

data class UserUseCases(
    val saveUserInfo: SaveUserInfo,
    val loadUserInfo: LoadUserInfo,
    val userGetIp: UserGetIp,
    val registerUser: RegisterUser,
    val updateCredentials: UpdateCredentials
)
