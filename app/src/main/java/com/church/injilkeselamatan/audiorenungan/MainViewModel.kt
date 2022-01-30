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
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.CreateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.data.data_source.remote.models.UpdateUserRequest
import com.church.injilkeselamatan.audiorenungan.feature_account.domain.repository.UserRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.AnotherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val anotherUseCases: AnotherUseCases
) : ViewModel() {

    private val _needSignIn = MutableSharedFlow<Boolean>()
    val needSignIn = _needSignIn.asSharedFlow()

    private var registerJob: Job? = null
    private var updateUserJob: Job? = null

    init {
        fetchSession()
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
                val ipResult = userRepository.getIp().data?.ipAddress
                    ?: "IpAddress not found"
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
                //Amplify.Auth.signOut()
                Log.e("AuthQuickstart", error.toString())
                _needSignIn.emit(true)
            }
        }
    }

    private suspend fun registerUser(createUserRequest: CreateUserRequest) {
        registerJob?.cancel()
        registerJob = viewModelScope.launch {
            userRepository.registerUser(createUserRequest).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val dataUser = resource.data
                        Log.i(TAG, "users on success register ${dataUser?.message}")
                        updateUserCredentials(createUserRequest.toUpdateUserRequest())
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "users on error register ${resource.message}")
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private suspend fun updateUserCredentials(userUpdate: UpdateUserRequest) {
        updateUserJob?.cancel()
        updateUserJob = viewModelScope.launch {
            userRepository.updateCredentials(userUpdate).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val dataUser = resource.data
                        Log.d(TAG, dataUser?.users.toString())
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "update User error: ${resource.message}")
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (str.isEmpty()) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

    fun copyToClipboard() {
        anotherUseCases.copyToClipboard()
    }
}

private const val TAG = "MainViewModel"