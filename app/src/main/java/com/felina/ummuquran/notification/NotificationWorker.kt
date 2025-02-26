package com.felina.ummuquran.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.felina.ummuquran.data.utils.NotificationHelper

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "It's time!"

        NotificationHelper.showNotification(applicationContext, title, message)

        return Result.success()
    }
}
