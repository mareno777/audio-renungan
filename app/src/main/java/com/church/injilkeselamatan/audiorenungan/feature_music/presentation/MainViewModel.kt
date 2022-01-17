package com.church.injilkeselamatan.audiorenungan.feature_music.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthException
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val isLoading = MutableStateFlow(true)

    private val _needSignIn = MutableSharedFlow<Boolean>()
    val needSignIn = _needSignIn.asSharedFlow()

    init {
        fetchSession()
    }


    private fun fetchSession() {
        viewModelScope.launch {
            try {
                delay(2000L)
                val session = Amplify.Auth.fetchAuthSession()
                Log.i("AuthQuickstart", "Auth session = $session")
                if (!session.isSignedIn) {
                    _needSignIn.emit(true)
                } else {
                    isLoading.emit(false)
                }
            } catch (error: AuthException) {
                Log.e("AuthQuickstart", "Failed to fetch auth session", error)
            }
        }
    }
}