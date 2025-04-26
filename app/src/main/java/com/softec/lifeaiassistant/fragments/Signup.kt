package com.softec.lifeaiassistant.fragments

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.digitaltrainer.customClasses.ErrorToast
import com.example.digitaltrainer.customClasses.SuccessToast
import com.softec.lifeaiassistant.customClasses.AuthenticationResponses
import com.softec.lifeaiassistant.customClasses.SignupViewModelFactory
import com.softec.lifeaiassistant.databinding.FragmentSignupBinding
import com.softec.lifeaiassistant.repository.AuthRepository
import com.softec.lifeaiassistant.viewModel.SignupViewModel

class Signup : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        val repository = AuthRepository()
        viewModel =
                ViewModelProvider(this, SignupViewModelFactory(repository))[SignupViewModel::class.java]

        setupObservers()
        binding.btnSignup.setOnClickListener { handleSignup() }
        return binding.root
    }

    private fun handleSignup() {
        binding.apply {
            hideKeyboard()
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (validateInput(name,email,password,confirmPassword)){
                btnSignup.isEnabled = false
                binding.btnSignup.text = null
                progressBar.visibility = View.VISIBLE
                viewModel.createUser(name,email,password)
            }
        }
    }
    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        binding.apply {

            val nameRegex = "^[A-Za-z\\s]{2,50}$".toRegex()
            if (!nameRegex.matches(name.trim())) {
                etName.error = "Enter a valid name (only letters & spaces, min 2 chars)"
                return false
            }

            // Email validation using regex (additional check)
            val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !emailRegex.matches(email.trim())) {
                etEmail.error = "Invalid email format"
                return false
            }

            // Password security check (Min 8 chars, 1 letter, 1 number, 1 special char)
            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[A-Za-z\\d\\S]{6,}\$".toRegex()
            if (!passwordRegex.matches(password)) {
                etPassword.error = "Password must have at least 6 chars, 1 letter, 1 number & 1 special character"
                return false
            }

            // Check if passwords match
            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match"
                return false
            }
        }
        return true
    }


    private fun setupObservers() {
        viewModel.signupState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is AuthenticationResponses.Loading -> {
                    binding.btnSignup.text = null
                    binding.btnSignup.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }

                is AuthenticationResponses.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.text = "Signup"
                    binding.btnSignup.isEnabled = true
                    showToast(resource.data, isSuccess = true)
                    clearFields()
                }

                is AuthenticationResponses.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.text = "Signup"
                    binding.btnSignup.isEnabled = true
                    showToast(resource.message, isSuccess = false)

                }
            }
        }
    }

    private fun clearFields() {
        binding.apply {
            etEmail.text = null
            etName.text = null
            etPassword.text = null
            etConfirmPassword.text = null
            etConfirmPassword.clearFocus()
            etName.clearFocus()
            etEmail.clearFocus()
            etPassword.clearFocus()
        }
    }

    private fun showToast(message: String, isSuccess: Boolean) {
        if (isSuccess) {
            val successToast = SuccessToast(requireContext())
            successToast.setText(message)
            successToast.show()
        } else {
            val errorToast = ErrorToast(requireContext())
            errorToast.setText(message)
            errorToast.show()
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity?.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
/*
*
class Signup : Fragment() {

    private val TAG = "Signup Fragment"
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        initializer()
        return binding.root
    }

    private fun initializer() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        binding.btnSignup.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        try {
            binding.apply {
                hideKeyboard()
                btnSignup.isEnabled = false
                btnSignup.text = null
                progressBar.visibility = View.VISIBLE
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (validateInput(name, email, password, confirmPassword)) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: ""
                                val passwordHash = generateHash(password, "user")
                                val user = User(userId, name, email, passwordHash, "")
                                database.child("users").child(userId).setValue(user)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            auth.currentUser?.sendEmailVerification()
                                                ?.addOnCompleteListener { verificationTask ->
                                                    if (verificationTask.isSuccessful) {
                                                        val toast = SuccessToast(requireContext())
                                                        toast.setText("Please verify your email.")
                                                        toast.show()
                                                        onSuccessResult()
                                                    } else {
                                                        val toast = ErrorToast(requireContext())
                                                        toast.setText("Failed to register user!")
                                                        toast.show()
                                                    }
                                                }
                                        } else {
                                            val toast = ErrorToast(requireContext())
                                            toast.setText("Failed to register user!")
                                            toast.show()
                                        }
                                    }
                            } else {
                                val toast = ErrorToast(requireContext())
                                toast.setText("Registration Failed: ${task.exception?.message}")
                                toast.show()
                            }
                            progressBar.visibility = View.GONE
                            btnSignup.isEnabled = true
                            btnSignup.text = "Signup"
                        }
                } else {
                    progressBar.visibility = View.GONE
                    btnSignup.isEnabled = true
                    btnSignup.text = "Signup"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "createUser: ${e.message}")
            val toast = ErrorToast(requireContext())
            toast.setText("Failed to register user!")
            toast.show()
        }
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity?.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onSuccessResult() {
        binding.apply {
            etEmail.text = null
            etName.text = null
            etPassword.text = null
            etConfirmPassword.text = null
            etConfirmPassword.clearFocus()
            etName.clearFocus()
            etEmail.clearFocus()
            etPassword.clearFocus()
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        binding.apply {
            if (name.isEmpty()) {
                etName.error = "Name cannot be empty"
                return false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = ("Invalid email format")
                return false
            }
            if (password.length < 6) {
                etPassword.error = ("Password must be at least 6 characters")
                return false
            }
            if (password != confirmPassword) {
                etConfirmPassword.error = ("Passwords do not match")
                return false
            }
        }
        return true
    }

    private fun generateHash(input: String, key: String): String {
        val inputBytes = input.toByteArray(Charsets.UTF_8)
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val outputBytes = ByteArray(inputBytes.size)

        for (i in inputBytes.indices) {
            outputBytes[i] = (inputBytes[i] xor keyBytes[i % keyBytes.size])
        }
        return Base64.getEncoder().encodeToString(outputBytes)
    }
}*/