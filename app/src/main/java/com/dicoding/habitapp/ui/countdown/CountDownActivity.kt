package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class CountDownActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var viewModel: CountDownViewModel
    private var isCountdownRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)
        viewModel.setInitialTime(habit.minutesFocus)

        val countDownTextView: TextView = findViewById(R.id.tv_count_down)
        viewModel.currentTimeString.observe(this) { timeString ->
            countDownTextView.text = timeString
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.startTimer()
            isCountdownRunning = true
            updateButtonState(isCountdownRunning)
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer(true)
            isCountdownRunning = false
            updateButtonState(isCountdownRunning)
        }

        viewModel.eventCountDownFinish.observe(this) { eventCountDownFinish ->
            if (eventCountDownFinish) {
                isCountdownRunning = false
                updateButtonState(isCountdownRunning)
                showCountDownFinishMessage()

                val inputData = workDataOf(
                    HABIT_ID to habit.id,
                    HABIT_TITLE to habit.title
                )

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .build()

                val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(inputData)
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(this).enqueueUniqueWork(
                    NOTIF_UNIQUE_WORK,
                    ExistingWorkPolicy.REPLACE,
                    notificationWorkRequest
                )
            }
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }

    private fun showCountDownFinishMessage() {
        Toast.makeText(this, "Countdown finished!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetTimer(false)
    }
}
