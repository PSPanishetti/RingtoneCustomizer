package com.app.ringtonecustomizer

import android.app.Application
import android.content.Intent
import com.pixplicity.easyprefs.library.Prefs

class ApplicationManager : Application() {

    override fun onCreate() {
        super.onCreate()
        val service: Intent = Intent(
            this,
            PhoneStateBroadcastReceiver::class.java
        )

        startService(service)

        Prefs.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}