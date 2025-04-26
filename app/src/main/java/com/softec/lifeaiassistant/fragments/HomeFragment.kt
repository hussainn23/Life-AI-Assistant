package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.firebase.firestore.FirebaseFirestore
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.databinding.HomeFragmentBinding
import com.softec.lifeaiassistant.databinding.LayoutFragmentHomeBinding
import com.softec.lifeaiassistant.models.ModelUser
import com.softec.lifeaiassistant.utils.SharedPrefManager

class HomeFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {
//        home_fragment.xml

    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedPrefManager: SharedPrefManager



    override fun onCreate() {
        sharedPrefManager = SharedPrefManager(context)
        getUser(sharedPrefManager.getUserId())


        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {
                }

                override fun onFinish() {
                    initiateLayout()
                }
            }.start()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "initiateData: " + e.message)
        }
    }

    private fun initiateLayout() {
        settingUpBinding()
    }

    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = HomeFragmentBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }

    }


    private fun getUser(userId: String?) {
        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)
        Toast.makeText(context, "debug ; "+userId, Toast.LENGTH_SHORT).show()


        db.collection("Users") // Your collection name
            .whereEqualTo("userId", id) // Comparing the userId field
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents.firstOrNull()
                    if (document != null) {
                        val user = document.toObject(ModelUser::class.java)
                        if (user != null) {
                            sharedPrefManager.saveUser(user)
                            Toast.makeText(context, "User: ${user.userName}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to parse user", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
