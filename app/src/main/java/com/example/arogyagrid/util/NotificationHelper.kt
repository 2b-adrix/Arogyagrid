package com.example.arogyagrid.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.arogyagrid.MainActivity
import com.example.arogyagrid.R

object NotificationHelper {
    const val CHANNEL_CRITICAL_ALERTS = "arogyagrid_critical_alerts"
    const val CHANNEL_TRANSFERS = "arogyagrid_transfers"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL_ALERTS,
                "Critical Healthcare Shortages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent stockout risks and priority medicine shortages (< 3 days)"
                enableVibration(true)
            }

            val transferChannel = NotificationChannel(
                CHANNEL_TRANSFERS,
                "Resource Redistribution Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on approved or completed inter-facility medicine transfers"
            }

            notificationManager.createNotificationChannel(criticalChannel)
            notificationManager.createNotificationChannel(transferChannel)
        }
    }

    fun showCriticalStockAlert(
        context: Context,
        medicineName: String,
        facilityName: String,
        daysRemaining: Int,
        notificationId: Int = 101
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CRITICAL_ALERTS)
            .setSmallIcon(R.drawable.arogyagride)
            .setContentTitle("CRITICAL SHORTAGE: $medicineName")
            .setContentText("$facilityName has only $daysRemaining days of stock remaining!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Urgent stockout risk detected at $facilityName. Only $daysRemaining days of $medicineName remaining. Redistribution transfer recommended immediately.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showTransferApprovedNotification(
        context: Context,
        medicineName: String,
        quantity: Int,
        destinationFacility: String,
        notificationId: Int = 102
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TRANSFERS)
            .setSmallIcon(R.drawable.arogyagride)
            .setContentTitle("Transfer Dispatched: $medicineName")
            .setContentText("$quantity units dispatched to $destinationFacility")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
