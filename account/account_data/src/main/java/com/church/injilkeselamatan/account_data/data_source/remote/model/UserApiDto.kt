package com.church.injilkeselamatan.account_data.data_source.remote.model

import com.church.injilkeselamatan.account_domain.model.UserApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserApiDto(
    val code: Int,
    @SerialName("data")
    val users: UserDto?,
    val message: String
) {
    fun toUserApiSingle(): UserApi {
        return UserApi(
            code = code,
            users = users?.toUser(),
            message = message
        )
    }
}
