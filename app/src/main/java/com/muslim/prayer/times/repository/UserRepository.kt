package com.muslim.prayer.times.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muslim.prayer.times.utils.PrayTime
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserRepository @Inject constructor() {
    val _userResponseLiveData = MutableLiveData<ArrayList<String>>()
    val userResponseLiveData: LiveData<ArrayList<String>> get() = _userResponseLiveData

    fun prayerstimes(location: Location): ArrayList<String> {

        val latitude = location.latitude //33.6105566
        val longitude = location.longitude //73.00463544

        //Get NY time zone instance

        //Get NY time zone instance
        val defaultTz = TimeZone.getDefault()

        //Get NY calendar object with current date/time

        //Get NY calendar object with current date/time
        val defaultCalc = Calendar.getInstance(defaultTz)

        //Get offset from UTC, accounting for DST

        //Get offset from UTC, accounting for DST
        val defaultTzOffsetMs = defaultCalc[Calendar.ZONE_OFFSET] + defaultCalc[Calendar.DST_OFFSET]
        val timezone = (defaultTzOffsetMs / (1000 * 60 * 60)).toDouble()

        // Test Prayer times here
        // Test Prayer times here
        val prayers = PrayTime()

        prayers.timeFormat = PrayTime.TIME_12
        prayers.calcMethod = PrayTime.KARACHI
        prayers.asrJuristic = PrayTime.SHAFII
        prayers.adjustHighLats = PrayTime.ANGLE_BASED

        val offsets =
            intArrayOf(0, 0, 0, 0, 0, 0, 0) // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}

        prayers.tune(offsets)

        val now = Date()
        val cal = Calendar.getInstance()
        cal.time = now

        val prayerTimes = prayers.getPrayerTimes(
            cal,
            latitude, longitude, timezone
        )
        val prayerNames = prayers.timeNames
        for (i in prayerTimes.indices) {
            println(prayerNames[i] + " - " + prayerTimes[i])

            Log.e("TAG", "this is pencil and: ${prayerNames[i]} vc ${prayerTimes[i]}")
        }
        _userResponseLiveData.postValue(prayerNames)
        return prayerTimes
    }
}
