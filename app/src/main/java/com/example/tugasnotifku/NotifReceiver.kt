package com.example.tugasnotifku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

// untuk menangani broadcast intents yang dikirim dalam aplikasi Android,
// terutama untuk menangkap aksi "LIKE" dan "DISLIKE"

class NotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) { // Dipanggil ketika NotifReceiver menerima broadcast intent.
        val action = intent?.action
        val actionType = when (action) { // Mengecek action
            "com.example.LIKE_ACTION" -> "LIKE"
            "com.example.DISLIKE_ACTION" -> "DISLIKE"
            else -> null
        }

        // Jika actionType tidak null, pesan toast akan ditampilkan berdasarkan jenis aksi
        if (actionType != null) {
            val toastMessage = if (actionType == "LIKE") "Suka!" else "Tidak Suka!"
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()

            // Kirim intent ke MainActivity untuk memperbarui UI
            val updateIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("ACTION", actionType)
            }
            context?.startActivity(updateIntent) // Memulai MainActivity dengan intent yang baru dibuat.
        }
    }
}
