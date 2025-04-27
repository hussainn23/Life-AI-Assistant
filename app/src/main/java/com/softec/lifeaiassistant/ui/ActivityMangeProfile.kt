package com.softec.lifeaiassistant.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppDialogBuilder
import com.softec.lifeaiassistant.customClasses.ButtonMaterial2
import com.softec.lifeaiassistant.databinding.ActivityMangeProfileBinding
import com.softec.lifeaiassistant.models.ModelUser
import com.softec.lifeaiassistant.utils.SharedPrefManager

class ActivityMangeProfile : AppCompatActivity() {
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var dialogName: AppDialogBuilder
    private lateinit var binding: ActivityMangeProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefManager = SharedPrefManager(this)
        val user = sharedPrefManager.getUser()
        binding.ivBack.setOnClickListener { finish() }
        binding.changeName.setText("Change Name", user?.userName)
        binding.changeName.setOnClickListener {
            dialogName = AppDialogBuilder(this, R.layout.dialog_change_name).apply {
                findViewById<ShapeableImageView>(R.id.ivBack).setOnClickListener { dismiss() }
                findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { dismiss() }
                val etName = findViewById<EditText>(R.id.etName)
                etName.setText(user!!.userName)
                val btn = findViewById<ButtonMaterial2>(R.id.btnLogout)
                btn.setOnClickListener {
                    val newName = etName.text.toString()
                    if (newName.isEmpty()) {
                        findViewById<TextInputLayout>(R.id.etLay).error = "Field cannot be empty"
                    } else {
                        val newUser = ModelUser(
                            newName,
                            user.userEmail,
                            user.passHash,
                            user.imageUrl,
                            user.update.toString()
                        )
                        sharedPrefManager.saveUser(newUser)
                        Toast.makeText(
                            this@ActivityMangeProfile,
                            "Name changed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogName.dismiss()
                    }
                }

            }
            dialogName.show()

        }


    }
}