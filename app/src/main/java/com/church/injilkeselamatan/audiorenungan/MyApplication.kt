package com.church.injilkeselamatan.audiorenungan

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.kotlin.core.Amplify
import dagger.hilt.android.HiltAndroidApp
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

        CoroutineScope(Dispatchers.IO).launch {
            Amplify.Auth.signOut()
            try {
                val result = Amplify.Auth.signIn("username", "Password1")
                if (result.isSignInComplete) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.e("AuthQuickstart", "Sign in not complete")
                }
            } catch (error: AuthException) {
                Log.e("AuthQuickstart", "${error.cause}")
            }
            try {
                val session = Amplify.Auth.fetchAuthSession()
                Log.i("AmplifyQuickstart", "Auth session = $session")
            } catch (error: AuthException) {
                Log.e("AmplifyQuickstart", error.toString())
            }
        }
    }
}