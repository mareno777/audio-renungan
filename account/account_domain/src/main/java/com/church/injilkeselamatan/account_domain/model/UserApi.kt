package com.church.injilkeselamatan.account_domain.model


data class UserApi(
    val code: Int,
    val users: User?,
    val message: String
)
