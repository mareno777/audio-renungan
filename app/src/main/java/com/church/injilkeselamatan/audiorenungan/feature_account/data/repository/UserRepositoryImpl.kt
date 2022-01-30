package com.church.injilkeselamatan.audiorenungan.feature_account.data.repository

import android.util.Log
import com.church.injilkeselamatan.audiorenungan.BuildConfig
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.*
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val client: HttpClient) : UserRepository {

    private val endpointUrl = "${BuildConfig.BASE_URL}/users"

    override suspend fun getUsers(): Flow<Resource<List<UserDto>>> = flow {
        try {
            emit(Resource.Loading())
            val result = client.get<UserApiDto>(endpointUrl)
            emit(Resource.Success(result.users))
            Log.i("UserRepository", result.message)

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun registerUser(createUserRequest: CreateUserRequest): Flow<Resource<UserApiDtoSingle>> =
        flow {
            try {
                emit(Resource.Loading())
                val result = client.post<UserApiDtoSingle>(endpointUrl) {
                    contentType(ContentType.Application.Json)
                    body = createUserRequest
                    method = HttpMethod.Post
                }
                emit(Resource.Success(result))
                Log.i("UserRepository", result.message)

            } catch (e: ClientRequestException) {
                val userApiDtoSingle = UserApiDtoSingle(405, null, "User already exists!")
                emit(Resource.Success(userApiDtoSingle))
                Log.e("UserRepository", e.toString())
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
        }

    override suspend fun updateCredentials(updateUserRequest: UpdateUserRequest): Flow<Resource<UserApiDtoSingle>> =
        flow {
            try {
                emit(Resource.Loading())
                val result =
                    client.put<UserApiDtoSingle>("$endpointUrl/${updateUserRequest.email}") {
                        contentType(ContentType.Application.Json)
                        body = updateUserRequest
                        method = HttpMethod.Put
                    }
                emit(Resource.Success(result))
                Log.i("UserRepository", "update success ${result.message}")

            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
                Log.e("UserRepository", "update error $e")

            }
        }

    override suspend fun getIp(): Resource<GetIpAddress> {
        return try {
            val result = client.get<GetIpAddress>("https://api.ipify.org/?format=json")
            Log.d("UserRepository", result.ipAddress)
            Resource.Success(result)
        } catch (e: Exception) {
            Log.e("UserRepository", "error ip address$e")
            Resource.Error(
                message = e.message ?: "Unknown error occurred",
                data = GetIpAddress("Couldn't fetch IpAddress")
            )
        }
    }
}