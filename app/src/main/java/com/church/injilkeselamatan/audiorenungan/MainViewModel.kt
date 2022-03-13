package com.church.injilkeselamatan.audiorenungan

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.kotlin.core.Amplify
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.CreateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.model.UpdateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.use_case.UserUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.AnotherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val anotherUseCases: AnotherUseCases
) : ViewModel() {

    private val _needSignIn = MutableSharedFlow<Boolean>()
    val needSignIn = _needSignIn.asSharedFlow()

    val needShare = MutableSharedFlow<Boolean>()

    private val _needUpdate = MutableSharedFlow<Boolean>()
    val needUpdate = _needUpdate.asSharedFlow()

    private var updateUserJob: Job? = null

    init {
        checkLatestVersion()
    }

    fun fetchSession() {
        viewModelScope.launch {
            try {
                val session = Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession
                val id = session.identityId
                if (id.type == AuthSessionResult.Type.SUCCESS) {
                    Log.i("AuthQuickStart", "IdentityId: ${id.value}")
                    getAuthenticatedUser()

                } else if (id.type == AuthSessionResult.Type.FAILURE) {
                    Log.i("AuthQuickStart", "IdentityId not present: ${id.error}")
                    _needSignIn.emit(true)
                }
            } catch (error: AuthException) {
                Log.e("AuthQuickstart", "Failed to fetch auth session", error)
            } catch (error: UserNotFoundException) {
                Log.e("AuthQuickstart", "User not Found", error)
            }
        }
    }

    private fun getAuthenticatedUser() {
        viewModelScope.launch {
            try {
                val attributtes = Amplify.Auth
                    .fetchUserAttributes()
                val email = attributtes.last().value
                val profilePicture = attributtes[0].value
                val name = attributtes[1].value
                val ipResult = userUseCases.userGetIp()
                val createUserRequest = CreateUserRequest(
                    email = email,
                    name = name,
                    phoneNumber = Random.nextInt().toString(),
                    model = getDeviceName(),
                    ipAddress = ipResult,
                    profile = profilePicture,
                )
                registerUser(createUserRequest)
            } catch (error: AuthException) {
                Log.e("AuthQuickstart", error.toString())
                _needSignIn.emit(true)
            }
        }
    }

    private suspend fun registerUser(createUserRequest: CreateUserRequest) {
        val result = userUseCases.registerUser(createUserRequest)
        if (result.isSuccess) {
            updateUserCredentials(createUserRequest.toUpdateUserRequest())
        }
        when (val exception = result.exceptionOrNull()) {
            is ClientRequestException -> {
                if (exception.response.status == HttpStatusCode.MethodNotAllowed) {
                    updateUserCredentials(createUserRequest.toUpdateUserRequest())
                }
            }
        }
    }

    private fun updateUserCredentials(updateUserRequest: UpdateUserRequest) {
        updateUserJob?.cancel()
        updateUserJob = viewModelScope.launch {
            userUseCases.saveUserInfo(
                updateUserRequest.toUserInfo()
            )
            userUseCases.updateCredentials(updateUserRequest)
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(value: String): String {
        if (value.isEmpty()) {
            return value
        }
        val charArray = value.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (char in charArray) {
            if (capitalizeNext && Character.isLetter(char)) {
                phrase.append(Character.toUpperCase(char))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(char)) {
                capitalizeNext = true
            }
            phrase.append(char)
        }
        return phrase.toString()
    }

    fun copyToClipboard() {
        anotherUseCases.copyToClipboard()
    }

    fun shareIntent() {
        viewModelScope.launch {
            needShare.emit(true)
        }
    }

    fun emailIntent() {
        anotherUseCases.emailIntent()
    }

    private fun checkLatestVersion() {
        viewModelScope.launch {
            if (anotherUseCases.checkVersion.isLatestVersion()) {
                _needUpdate.emit(false)
                fetchSession()
            } else {
                _needUpdate.emit(true)
            }
        }
    }
}