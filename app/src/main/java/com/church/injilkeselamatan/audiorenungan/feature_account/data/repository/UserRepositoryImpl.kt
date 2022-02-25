package com.church.injilkeselamatan.audiorenungan.feature_account.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.church.injilkeselamatan.audiorenungan.BuildConfig
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.local.model.UserInfo
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.*
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.CREATED_AT
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.EMAIL
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.IP_ADDRESS
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.LAST_LOGIN
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.MODEL
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.NAME
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.PHONE_NUMBER
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.PROFILE
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository.Companion.UPDATED_AT
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val client: HttpClient,
    private val context: Context
) : UserRepository {

    private val endpointUrl = "${BuildConfig.BASE_URL}/users"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")

    override suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val result = client.get<UserApiDto>(endpointUrl)
            Result.success(result.users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(createUserRequest: CreateUserRequest): Result<UserApiDtoSingle> {
        return try {
            val result = client.post<UserApiDtoSingle>(endpointUrl) {
                contentType(ContentType.Application.Json)
                body = createUserRequest
                method = HttpMethod.Post
            }
            Log.i("UserRepository", result.message)
            Result.success(result)
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

    override suspend fun updateCredentials(updateUserRequest: UpdateUserRequest): Result<UserApiDtoSingle> {
        return try {
            val result =
                client.put<UserApiDtoSingle>("$endpointUrl/${updateUserRequest.email}") {
                    contentType(ContentType.Application.Json)
                    body = updateUserRequest
                    method = HttpMethod.Put
                }
            Log.i("UserRepository", "update success ${result.message}")
            Result.success(result)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIp(): Result<GetIpAddress> {
        return try {
            val result = client.get<GetIpAddress>("https://api.ipify.org/?format=json")
            Result.success(result)
        } catch (e: Exception) {
            Log.e("UserRepository", "error ip address$e")
            Result.failure(e)
        }
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        context.dataStore.edit { userInfoPref ->
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
        return context.dataStore.data
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
            }.first()
    }
}