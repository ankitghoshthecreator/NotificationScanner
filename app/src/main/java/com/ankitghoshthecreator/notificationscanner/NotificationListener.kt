package com.ankitghoshthecreator.notificationscanner

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationText = sbn.notification.extras.getString("android.text") ?: ""
        val packageName = sbn.packageName

        // Check if notification starts with "FD" in any case
        if (notificationText.startsWith("FD", ignoreCase = true)) {
            Log.d("NotificationListener", "Notification from $packageName starts with FD: $notificationText")

            // Send broadcast to MainActivity with the notification content
            val intent = Intent("com.ankitghoshthecreator.notificationscanner.FD_NOTIFICATION")
            intent.putExtra("notificationText", notificationText)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No changes needed here
    }
}
