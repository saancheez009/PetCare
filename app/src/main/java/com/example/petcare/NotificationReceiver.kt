package com.example.petcare

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderText = intent.getStringExtra("reminder_text")

        // Llama al NotificationHelper para mostrar la notificación
        if (reminderText != null) {
            NotificationHelper.sendNotification(context, "Recordatorio", reminderText)
        }
    }
}