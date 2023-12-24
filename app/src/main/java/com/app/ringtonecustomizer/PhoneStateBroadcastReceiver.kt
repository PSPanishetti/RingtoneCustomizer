package com.app.ringtonecustomizer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.pixplicity.easyprefs.library.Prefs


class PhoneStateBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PhoneStateBroadcastReceiver", "Call Coming bro ${intent.toString()}")

        if (intent.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)!!
            val isRinging = Prefs.getBoolean(PrefsConstants.IS_RINGING)
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d("RingToneManager", "IDLE BRO with isRinging $isRinging")
                if (isRinging) {
                    setNewRingtone(context)
                    Prefs.putBoolean(PrefsConstants.IS_RINGING, true)
                }
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Prefs.putBoolean(PrefsConstants.IS_RINGING, true)
                Log.d("RingToneManager", "Ringing BRO with isRinging $isRinging")
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d("RingToneManager", "EXTRA_STATE_OFFHOOK BRO")
            }
        }
    }

    private fun setNewRingtone(context: Context) {
        Log.d(PrefsConstants.APP_TAG, "setNewRingtone  called")
        val getPath = Prefs.getString(PrefsConstants.URI_PATH)
        if (getPath != null) {
            val file = getRandomFileURI()
            if (file != null) {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE,
                    file
                );
            }
        } else {
            Log.d(PrefsConstants.APP_TAG, "Got the path as $getPath")
        }
    }

    private fun getRandomFileURI(): Uri? {
        val permanentList = Prefs.getOrderedStringSet(PrefsConstants.LIST_OF_RINGTONES, null)
        val playedList =
            Prefs.getOrderedStringSet(PrefsConstants.PLAYED_LIST, null) ?: mutableListOf<String>()
        var isFound = false
        var songPath: String? = null
        while (!isFound) {
            Log.d(PrefsConstants.APP_TAG, "Still not found so looping")
            if (permanentList.size == playedList.size) {
                playedList.clear()
                Log.d(PrefsConstants.APP_TAG, "No more songs were found hence cleared")
            }
            songPath = permanentList.random()
            if (!playedList.contains(songPath)) {
                isFound = true
                Log.d(
                    PrefsConstants.APP_TAG,
                    "Got Random Path which was not in list hence returning with $songPath"
                )
                playedList.add(songPath)
                Prefs.putOrderedStringSet(PrefsConstants.PLAYED_LIST, playedList.toMutableSet())
                Log.d(
                    PrefsConstants.APP_TAG,
                    "Saving New List $playedList"
                )
            }
        }
        return Uri.parse(songPath)
    }

}