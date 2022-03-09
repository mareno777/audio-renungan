package com.church.injilkeselamatan.audiorenungan.feature_update

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo

class InAppUpdate(private val activity: Activity) : InstallStateUpdatedListener {

    companion object {
        private const val MY_REQUEST_CODE = 500
    }

    private var appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    init {
        checkUpdate()
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            // Check if update is available
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        MY_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(activity.applicationContext, "$e", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                checkUpdate()
            }
        }
    }

    override fun onStateUpdate(state: InstallState) = Unit

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            // Check if update is available
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlow(
                        info,
                        activity,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(activity.applicationContext, "$e", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }
}
