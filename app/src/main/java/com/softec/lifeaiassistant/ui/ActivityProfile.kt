package com.softec.lifeaiassistant.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.databinding.ActivityProfileBinding

class ActivityProfile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupOnClicks()
    }

    private fun setupOnClicks() {
        binding.apply {
            //layManageProfile.setOnClickListener { ActivityNavigator.startActivity(this@ActivityProfile,ManageProfile::class.java) }
            tvWebsite.setOnClickListener { openWebsite() }
            tvCall.setOnClickListener { callUs() }
            tvWhatsapp.setOnClickListener { whatsappUs() }
            tvEmail.setOnClickListener { emailUs() }
            tvCheckUpdates.setOnClickListener { checkForUpdates() }
            tvRate.setOnClickListener { rateUs() }
            tvShareApp.setOnClickListener { shareApp() }
            
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out inCity-Stores app!")
            putExtra(Intent.EXTRA_TEXT, "Hey! Check out inCity-Stores awesome app: https://play.google.com/store/apps/details?id=${packageName}")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
    private fun rateUs() {
        val uri = Uri.parse("market://details?id=${packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")))
        }
    }
    private fun checkForUpdates() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
        }
        startActivity(intent)
    }
    private fun emailUs() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@aunalvi.com")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback / Support")
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@ActivityProfile, "No email app installed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun whatsappUs() {
        val phoneNumber = "+923081366556" // Change this@ActivityProfile to your number
        val url = "https://wa.me/$phoneNumber"
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this@ActivityProfile, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openWebsite() {
        val websiteUrl = "https://aunalvi.com/incity-stores"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(websiteUrl)
        }
        startActivity(intent)
    }
    private fun callUs() {
        val phoneNumber = "03081366556"
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
}