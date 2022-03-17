package com.church.injilkeselamatan.account_presentation.google_screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.church.injilkeselamatan.account_domain.GoogleApiContract
import com.church.injilkeselamatan.account_presentation.google_screen.components.GoogleButton
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun GoogleScreen() {

    val launcher = rememberLauncherForActivityResult(contract = GoogleApiContract()) { task ->
        val result = task?.result
        result?.let {

            val credentials = GoogleAuthProvider.getCredential(it.idToken, null)
            Firebase.auth.signInWithCredential(credentials)
            Log.i("GoogleSignResult", it.displayName.toString())
            Log.i("GoogleSignResult", it.email.toString())
            Log.i("GoogleSignResult", it.idToken.toString())
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val user = Firebase.auth.currentUser
                user
                    ?.getIdToken(true)
                    ?.addOnSuccessListener {
                        Log.i("newToken", it.token.toString())
                    }
                Log.i("google", "user: $user")
            },
        contentAlignment = Alignment.Center
    ) {
        GoogleButton {
            Firebase.auth.signOut()
            launcher.launch(1)
        }
    }
}