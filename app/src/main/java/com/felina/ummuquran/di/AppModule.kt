package com.felina.ummuquran.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.felina.ummuquran.R
import com.felina.ummuquran.data.local.Ramadan
import com.felina.ummuquran.data.local.RamadanDao
import com.felina.ummuquran.data.local.RamadanDatabase
import com.felina.ummuquran.data.network.ApiClient
import com.felina.ummuquran.data.network.ApiService
import com.felina.ummuquran.data.network.ApiService2
import com.felina.ummuquran.data.repository.QuranRepository
import com.felina.ummuquran.notification.NotificationWorker
import com.felina.ummuquran.ui.view.dashboard.DashboardViewModel
import com.felina.ummuquran.ui.view.quran.QuranViewModel
import com.felina.ummuquran.ui.view.read.ReadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

val appModule = module {
    // Provide ApiService
    single<ApiService> { ApiClient.retrofit }
    single<ApiService2> { ApiClient.retrofit2 }

    // Provide UserRepository
    single { QuranRepository(get(),get()) }

    // Provide UserViewModel
    viewModel {
        QuranViewModel(get())
    }
    viewModel {
        ReadViewModel(get())
    }
}

val databaseModule = module {
    single { provideDatabase(get()) }
    single { provideDao(get()) }

    viewModel {
        DashboardViewModel(get())
    }
}

fun provideDatabase(context: Context): RamadanDatabase {
    return Room.databaseBuilder(
        context,
        RamadanDatabase::class.java,
        "ramadan.db"
    ).addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = provideDao(provideDatabase(context))
                fillWithStartingData(context, dao)
            }
        }
    }).allowMainThreadQueries().build()
}
fun provideDao(database: RamadanDatabase): RamadanDao {
    return database.ramadanDao()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun fillWithStartingData(context: Context, dao: RamadanDao) {
    val jsonArray = loadJsonArray(context)
    try {
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                dao.insertAll(
                    Ramadan(
                        item.getInt("id"),
                        item.getString("title"),
                        item.getString("date"),
                        item.getString("startTime"),
                        item.getString("priorityLevel"),
                        false
                    )
                )
                scheduleNotification(
                    context = context,
                    timeInMillis = convertToMillis(dateString = item.getString("date"), timeString = item.getString("startTime")),
                    title = item.getString("title"),
                    message = "Reminder",
                )
            }
        }
    } catch (exception: JSONException) {
        exception.printStackTrace()
    }
}

private fun loadJsonArray(context: Context): JSONArray? {
    val builder = StringBuilder()
    val `in` = context.resources.openRawResource(R.raw.ramadan)
    val reader = BufferedReader(InputStreamReader(`in`))
    var line: String?
    try {
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        val json = JSONObject(builder.toString())
        return json.getJSONArray("ramadan")
    } catch (exception: IOException) {
        exception.printStackTrace()
    } catch (exception: JSONException) {
        exception.printStackTrace()
    }
    return null
}

fun scheduleNotification(context: Context, timeInMillis: Long, title: String, message: String) {
    val currentTime = System.currentTimeMillis()
    val delay = timeInMillis - currentTime

    if (delay <= 0) return

    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
@RequiresApi(Build.VERSION_CODES.O)
fun convertToMillis(dateString: String, timeString: String): Long {
    val date = LocalDate.parse(dateString)
    val time = LocalTime.parse(timeString)

    val dateTime = LocalDateTime.of(date, time)
    return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
