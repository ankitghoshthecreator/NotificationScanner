package com.ankitghoshthecreator.notificationscanner

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "notification_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create notification channel for Android O and above
        createNotificationChannel()

        // Request permissions when the app opens
        requestNotificationPermissions()

        // Set up the button to send notification
        val sendButton: Button = findViewById(R.id.button)
        val editText: EditText = findViewById(R.id.editTextText)

        sendButton.setOnClickListener {
            val message = editText.text.toString()
            if (message.isNotEmpty()) {
                sendNotification(message)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to send notification
    private fun sendNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Set the app icon
            .setContentTitle("New Notification")
            .setContentText(message) // Set the text from EditText
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Use NotificationManagerCompat to send the notification
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    // Method to create a notification channel (required for Android O and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val descriptionText = "Channel for app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Use NotificationManager to create the channel
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Request POST_NOTIFICATIONS permission and Notification Listener permission
    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val postNotificationsPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (!isGranted) {
                        Toast.makeText(this, "Notification permission is required to send notifications.", Toast.LENGTH_SHORT).show()
                    }
                }
            postNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!isNotificationServiceEnabled()) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            Toast.makeText(this, "Please enable notification access for this app.", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

    // Check if Notification Listener Service is enabled
    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName = packageName
        return enabledListeners != null && enabledListeners.contains(packageName)
    }
}
