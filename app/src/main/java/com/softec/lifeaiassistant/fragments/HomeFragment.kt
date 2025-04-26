package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
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
import com.softec.lifeaiassistant.repository.AuthRepository
import com.softec.lifeaiassistant.utils.SharedPrefManager
import com.softec.lifeaiassistant.viewModel.LoginViewModel

class HomeFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {
    //        home_fragment.xml
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var viewModel: LoginViewModel

    private lateinit var binding: HomeFragmentBinding


    override fun onCreate() {
        sharedPrefManager = SharedPrefManager(context)
        viewModel = LoginViewModel(AuthRepository())

        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {
                }
                override fun onFinish() {
                    getUser(sharedPrefManager.getUserId())
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
        db.collection("users") // Your collection name
            .whereEqualTo("userId", userId) // Comparing the userId field
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
