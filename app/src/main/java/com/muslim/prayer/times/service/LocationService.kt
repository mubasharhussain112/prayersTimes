package com.muslim.prayer.times.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.muslim.prayer.times.R
import com.muslim.prayer.times.repository.UserRepository
import com.muslim.prayer.times.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.muslim.prayer.times.utils.LocationHelper
import com.muslim.prayer.times.utils.MyLocationListener
import com.muslim.prayer.times.viewModel.PrayersTimesViewModel
import com.muslim.prayer.times.work.NotifyWork
import com.muslim.prayer.times.work.NotifyWork.Companion.NOTIFICATION_ID
import com.muslim.prayer.times.work.NotifyWork.Companion.NOTIFICATION_WORK
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.collections.ArrayList

class LocationService : Service() {
    val userRepository = UserRepository()

    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timer = Timer()
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mLocation = location
                    val arr = userRepository.prayerstimes(location!!)
                    addNotification(arr, 1)
                    addNotification(arr, 3)
                    addNotification(arr, 4)
                    addNotification(arr, 5)
                    addNotification(arr, 6)

                }
            })
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }

    private fun addNotification(prayersTimeList: ArrayList<String>, pos: Int) {
        val customCalendar = Calendar.getInstance()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        customCalendar.set(
            year,
            month,
            day,
            dateConversion(prayersTimeList[pos]).hours,
            dateConversion(prayersTimeList[pos]).minutes,
            0
        )
        val customTime = customCalendar.timeInMillis
        val currentTime = currentTimeMillis()
        if (customTime > currentTime) {
            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
            val delay = customTime - currentTime
            scheduleNotification(delay, data)
        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay, MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork
        ).enqueue()
    }

    fun dateConversion(date: String): Date {
        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm a")
        val date: Date = parseFormat.parse(date)
        return date
    }

}