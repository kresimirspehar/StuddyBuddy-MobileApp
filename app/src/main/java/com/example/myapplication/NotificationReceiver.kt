package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Primanje podataka iz alarma
        val title = intent.getStringExtra("title") ?: "Podsetnik"
        val notificationId = intent.getIntExtra("notificationId", 0)

        // Kreiranje NotificationManager-a
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Postavljanje Notification kanala za Android O i novije
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notify-todo",
                "Todo Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Kreiranje obaveštenja
        val notification = NotificationCompat.Builder(context, "notify-todo")
            .setSmallIcon(R.drawable.baseline_notifications_24) // Zamenite sa odgovarajućom ikonom
            .setContentTitle("Podsetnik")
            .setContentText("Podsetnik za zadatak: $title")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Prikazivanje obaveštenja
        notificationManager.notify(notificationId, notification)
    }
}
