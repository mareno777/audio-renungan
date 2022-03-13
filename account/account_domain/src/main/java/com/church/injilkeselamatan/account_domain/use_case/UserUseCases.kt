package com.church.injilkeselamatan.account_domain.use_case

data class UserUseCases(
    val saveUserInfo: SaveUserInfo,
    val loadUserInfo: LoadUserInfo,
    val userGetIp: UserGetIp,
    val registerUser: RegisterUser,
    val updateCredentials: UpdateCredentials
)
