package com.example.dfmsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode

import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import java.util.*


const val tag = "hello"

class MainActivity : AppCompatActivity() {
    lateinit var splitInstallManager: SplitInstallManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splitInstallManager = SplitInstallManagerFactory.create(applicationContext)
    }

    fun installActivity(view: View) {
        Toast.makeText(applicationContext, "install activity", Toast.LENGTH_SHORT).show()
        checkInstalled()
    }

    private fun checkInstalled() {
        Log.i(tag, "checkInstalled: ${splitInstallManager.installedModules.contains("dynamicfeature")}")
        if (splitInstallManager.installedModules.contains("dynamicfeature")) {
            splitInstallManager.installedModules.forEach{
                Log.i(tag, "checkInstalled: $it")
            }
            val dynamicClass=Class.forName("com.example.dynamicfeature.DynamicClass")
            if(dynamicClass is Class){
                dynamicClass.newInstance()
            }
        } else {
            Log.i("hello", "Registration feature is not installed")
            install()
        }
    }

    fun install() {
        var sessionId=0
        val listener = SplitInstallStateUpdatedListener { state: SplitInstallSessionState ->
            if (state.status() == SplitInstallSessionStatus.FAILED
                && state.errorCode() == SplitInstallErrorCode.SERVICE_DIED
            ) {
                // Retry the request.

                return@SplitInstallStateUpdatedListener
            }
            if (state.sessionId() == sessionId) {
                when (state.status()) {
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        Log.i(tag, "install: downloading")
                    }
                    SplitInstallSessionStatus.INSTALLED -> {
                        Log.i(tag, "install: installed")
                    }
                    SplitInstallSessionStatus.CANCELED -> {
                        Log.i(tag, "install: cancelled")
                    }
                    SplitInstallSessionStatus.CANCELING -> {
                        Log.i(tag, "install: cancelling")
                    }
                    SplitInstallSessionStatus.DOWNLOADED -> {
                        Log.i(tag, "install: downloaded")
                    }
                    SplitInstallSessionStatus.FAILED -> {
                        Log.i(tag, "install: failed")
                    }
                    SplitInstallSessionStatus.INSTALLING -> {
                        Log.i(tag, "install: installing")
                    }
                    SplitInstallSessionStatus.PENDING -> {
                        Log.i(tag, "install: pending")
                    }
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        Log.i(tag, "install: REQUIRES_USER_CONFIRMATION")
                    }
                    SplitInstallSessionStatus.UNKNOWN -> {
                        Log.i(tag, "install: unknown")
                    }
                }
            }
        }
        Log.i(tag, "installing")
        val request = SplitInstallRequest.newBuilder()
            .addModule("dynamicfeature")
            .build()
//        splitInstallManager.registerListener(listener)


        splitInstallManager.startInstall(request)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "installed", Toast.LENGTH_SHORT).show()
                sessionId=it
                Log.i(tag, "succeed$it")
                checkInstalled()
            }
            .addOnFailureListener {
                Log.i(tag, "failed ${it}")
            }

//    splitInstallManager.deferredInstall(Arrays.asList("dynamicfeature"))


    }

}