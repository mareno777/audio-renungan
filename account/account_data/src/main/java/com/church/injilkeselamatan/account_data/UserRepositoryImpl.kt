package com.church.injilkeselamatan.account_data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.church.injilkeselamatan.account_data.data_source.remote.model.*
import com.church.injilkeselamatan.account_domain.model.*
import com.church.injilkeselamatan.account_domain.repository.UserRepository
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.CREATED_AT
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.EMAIL
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.IP_ADDRESS
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.LAST_LOGIN
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.MODEL
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.NAME
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.PHONE_NUMBER
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.PROFILE
import com.church.injilkeselamatan.account_domain.repository.UserRepository.Companion.UPDATED_AT
import com.church.injilkeselamatan.core.dataStoreUser
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val client: HttpClient,
    private val context: Context
) : UserRepository {

    private val endpointUrl = "http://aws.injilkeselamatan.com:8080/users"

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val result = client.get<UsersApiDto>(endpointUrl)
            Result.success(result.users.map { it.toUser() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(createUserRequest: CreateUserRequest): Result<UserApi> {
        return try {
            val createUserRequestDto = CreateUserRequestDto(
                email = createUserRequest.email,
                name = createUserRequest.name,
                phoneNumber = createUserRequest.phoneNumber,
                updatedAt = createUserRequest.updatedAt,
                ipAddress = createUserRequest.ipAddress,
                lastLogin = createUserRequest.lastLogin,
                model = createUserRequest.model,
                profile = createUserRequest.profile
            )
            val result = client.post<UserApiDto>(endpointUrl) {
                contentType(ContentType.Application.Json)
                body = createUserRequestDto
                method = HttpMethod.Post
            }
            Result.success(result.toUserApiSingle())
        } catch (e: ClientRequestException) {
            Log.e(
                "UserRepository",
                "code: ${e.response.status.value} desc: ${e.response.status.description}"
            )
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCredentials(updateUserRequest: UpdateUserRequest): Result<UserApi> {
        return try {
            val updateUserRequestDto = UpdateUserRequestDto(
                email = updateUserRequest.email,
                name = updateUserRequest.name,
                phoneNumber = updateUserRequest.phoneNumber,
                updatedAt = updateUserRequest.updatedAt,
                ipAddress = updateUserRequest.ipAddress,
                lastLogin = updateUserRequest.lastLogin,
                model = updateUserRequest.model,
                profile = updateUserRequest.profile
            )
            val result =
                client.put<UserApiDto>("$endpointUrl/${updateUserRequest.email}") {
                    contentType(ContentType.Application.Json)
                    body = updateUserRequestDto
                    method = HttpMethod.Put
                }
            Log.i("UserRepository", "update success ${result.message}")
            Result.success(result.toUserApiSingle())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIp(): Result<GetIpAddress> {
        return try {
            val result = client.get<GetIpAddressDto>("https://api.ipify.org/?format=json")
            Result.success(result.toGetIpAddress())
        } catch (e: Exception) {
            Log.e("UserRepository", "error ip address$e")
            Result.failure(e)
        }
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        context.dataStoreUser.edit { userInfoPref ->
            userInfoPref[EMAIL] = userInfo.email
            userInfoPref[NAME] = userInfo.name
            userInfoPref[PHONE_NUMBER] = userInfo.phoneNumber
            userInfoPref[CREATED_AT] = userInfo.createdAt ?: 0L
            userInfoPref[UPDATED_AT] = userInfo.updatedAt ?: 0L
            userInfoPref[IP_ADDRESS] = userInfo.ipAddress
            userInfoPref[LAST_LOGIN] = userInfo.lastLogin ?: 0L
            userInfoPref[MODEL] = userInfo.model
            userInfoPref[PROFILE] = userInfo.profile
        }
    }

    override suspend fun loadUserInfo(): UserInfo? {
        return context.dataStoreUser.data
            .map { preferences ->
                val email = preferences[EMAIL]
                val phoneNumber = preferences[PHONE_NUMBER]
                if (email != null && phoneNumber != null) {
                    UserInfo(
                        email = email,
                        name = preferences[NAME] ?: "",
                        phoneNumber = phoneNumber,
                        createdAt = preferences[CREATED_AT] ?: 0L,
                        updatedAt = preferences[UPDATED_AT] ?: 0L,
                        ipAddress = preferences[IP_ADDRESS] ?: "",
                        lastLogin = preferences[LAST_LOGIN] ?: 0L,
                        model = preferences[MODEL] ?: "",
                        profile = preferences[PROFILE] ?: ""
                    )
                } else null
            }.firstOrNull()
    }
}