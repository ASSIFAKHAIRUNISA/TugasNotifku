package com.example.tugasnotifku

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.tugasnotifku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences // menyimpan dan membaca data lokal (jumlah likes/dislikes).

    // Variabel untuk menyimpan jumlah likes dan dislikes.
    private var likes = 0
    private var dislikes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        likes = sharedPreferences.getInt("LIKES", 0)
        dislikes = sharedPreferences.getInt("DISLIKES", 0)

        // Buat Notification Channel
        createNotificationChannel()

        // Tangkap intent dari NotifReceiver
        val action = intent.getStringExtra("ACTION")
        if (action != null) {
            handleAction(action)
        }

        // Update UI awal
        updateUI()

        // Tombol untuk mengirim notifikasi
        binding.btnSendNotif.setOnClickListener {
            sendNotification()
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification() {
        // Intent untuk aksi Like
        val likeIntent = Intent("com.example.LIKE_ACTION")
        likeIntent.setClass(this, NotifReceiver::class.java)
        val likePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            likeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent untuk aksi Dislike
        val dislikeIntent = Intent("com.example.DISLIKE_ACTION")
        dislikeIntent.setClass(this, NotifReceiver::class.java)
        val dislikePendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            dislikeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notifikasi
        val notification = NotificationCompat.Builder(this, "notif_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Apakah kamu suka Inside Out 2?")
            .setContentText("Pilih salah satu")
            .setLargeIcon(resources.getDrawable(R.drawable.img, null).toBitmap())
            .addAction(R.drawable.like, "Like", likePendingIntent)
            .addAction(R.drawable.dislike, "Dislike", dislikePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Kirim notifikasi
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1001, notification)
    }

    // Menangani Aksi dari Intent
    private fun handleAction(action: String) {
        when (action) {
            "LIKE" -> likes++
            "DISLIKE" -> dislikes++
        }
        savePreferences()
        updateUI()
    }

    // Memperbarui UI
    private fun updateUI() {
        binding.tvLikes.text = likes.toString()
        binding.tvDislikes.text = dislikes.toString()
    }

    // Menyimpan jumlah likes dan dislikes ke SharedPreferences.
    private fun savePreferences() {
        with(sharedPreferences.edit()) {
            putInt("LIKES", likes)
            putInt("DISLIKES", dislikes)
            apply()
        }
    }

    // Membuat Notifikasi Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Notification Channel"
            val descriptionText = "This is a notification channel."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("notif_channel", channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
