package com.example.clockproject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BackgroundService : Service() {

    private val TAG = "BackgroundService"
    private var isCounting = true
    private var time = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startChronometerThread()
        return START_STICKY
    }

    override fun onDestroy() {
        isCounting = false
        super.onDestroy()
    }

    private fun startChronometerThread() {
        Thread {
            while (isCounting) {
                try {
                    Thread.sleep(1000) // Increment every second
                    time++
                    val timeString = formatTime(time)

                    // Update notification with the time
                    val notification = createNotification("Elapsed Time: $timeString")
                    val notificationManager = getSystemService(NotificationManager::class.java)
                    notificationManager?.notify(1, notification)
                } catch (e: InterruptedException) {
                    isCounting = false
                }
            }
        }.start()
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, "BackgroundServiceChannel")
            .setContentTitle("Background Chronometer")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setOngoing(true) // Keeps the notification alive even if the user clears notifications
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "BackgroundServiceChannel",
                "Background Chronometer",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
