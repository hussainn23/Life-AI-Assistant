package com.softec.lifeaiassistant.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.digitaltrainer.activities.WelcomeNote
import com.example.digitaltrainer.customClasses.ActivityNavigator
import com.example.digitaltrainer.customClasses.AppDialogBuilder
import com.example.digitaltrainer.customClasses.ErrorToast
import com.example.digitaltrainer.customClasses.SuccessToast
import com.example.digitaltrainer.databinding.DialogForgotPasswordBinding
import com.example.digitaltrainer.databinding.FragmentLoginBinding
import com.example.digitaltrainer.repositories.AuthRepository
import com.example.digitaltrainer.viewModels.LoginViewModel
import com.example.digitaltrainer.viewModels.factories.LoginViewModelFactory
import com.example.digitaltrainer.viewModels.responsesClasses.AuthResult
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppDialogBuilder
import com.softec.lifeaiassistant.databinding.DialogForgotPasswordBinding
import com.softec.lifeaiassistant.databinding.FragmentLoginBinding
import com.softec.lifeaiassistant.viewModel.LoginViewModel


@Suppress("DEPRECATION")
class Login : Fragment() {

    private lateinit var dialogBinding: DialogForgotPasswordBinding
    private lateinit var loadingDialog: AppDialogBuilder
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository())
    }
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        animateParentEnter()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInput(email, password)) {
                hideKeyboard()
                viewModel.login(email, password)
            }
        }

        binding.ivGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnForgotPass.setOnClickListener {
            forgotPasswordDialog()
        }


        observeViewModel()
        return binding.root
    }

    private fun forgotPasswordDialog() {
        dialogBinding =
            DialogForgotPasswordBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            ivBack.setOnClickListener { bottomSheetDialog.dismiss() }
            btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
            btnConfirm.setOnClickListener {
                val email = etEmailForgotPass.text.toString().trim()
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmailForgotPass.error = "Invalid email address!"
                } else {
                    hideKeyboard()
                    viewModel.resetPassword(email)
                }

            }
        }
        bottomSheetDialog.show()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    if (binding.checkboxRemember.isChecked) keepUserLogged(1)
                    navigateToNextScreen()
                }

                is AuthResult.EmailNotVerified -> {
                    showToast("Please verify your email before logging in.", isSuccess = false)
                }

                is AuthResult.Failure -> {
                    showToast("Login Failed: ${result.message}", isSuccess = false)
                }

            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = null
                } else {
                    progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                }
            }
        }
        viewModel.resetStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    showToast("Password reset email sent. Check your email inbox", isSuccess = true)
                    bottomSheetDialog.dismiss()
                }

                is AuthResult.Failure -> {
                    showToast("Error: ${result.message}", isSuccess = false)
                }

                AuthResult.EmailNotVerified -> {

                }
            }
        }

        viewModel.loadingDialog.observe(viewLifecycleOwner) { isLoading ->
            dialogBinding.apply {
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                    btnConfirm.isEnabled = false
                    btnConfirm.text = null
                } else {
                    progressBar.visibility = View.GONE
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm"
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        // Email format check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return false
        }

        // Prevent SQL Injection: Ensure email only contains valid characters
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        if (!emailRegex.matches(email)) {
            binding.etEmail.error = "Invalid characters in email"
            return false
        }

        // Password security checks
        if (password.isEmpty()) {
            binding.etPassword.error = "Password cannot be empty"
            return false
        }

        return true
    }


    private fun showToast(message: String, isSuccess: Boolean) {
        if (isSuccess) {

//            CustomSnackBar.make(findViewById<ViewGroup>(android.R.id.), message, CustomSnackBar.MessageType.SUCCESS).show()
//            CustomSnackBar.make(createDimOverlay(requireContext()), message, CustomSnackBar.MessageType.SUCCESS).show()

//            requireActivity().window.setDimAmount(0.5f)

            val successToast = SuccessToast(requireContext())
            successToast.setText(message)
            successToast.show()
        } else {
//            CustomSnackBar.make(createDimOverlay(requireContext()), message, CustomSnackBar.MessageType.ERROR).show()
//            CustomSnackBar.make(binding.btnLogin, message, CustomSnackBar.MessageType.ERROR).show()
//            requireActivity().window.setDimAmount(0.5f)

            val errorToast = ErrorToast(requireContext())
            errorToast.setText(message)
            errorToast.show()
        }
    }

    private fun createDimOverlay(context: Context): View {
        val overlay = View(context)
        overlay.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
        overlay.alpha = 0.5f // Adjust the transparency level
        return overlay
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity?.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun navigateToNextScreen() {
        ActivityNavigator.startActivity(requireActivity(),WelcomeNote::class.java, clearStack = true)
        requireActivity().finish()
    }


    private fun signInWithGoogle() {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder()
                .build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()

        loadingDialog = AppDialogBuilder(requireContext())
        loadingDialog.setContentView(R.layout.layout_progress_dialog)
        loadingDialog.isCancelable = false
        loadingDialog.switchToWindowContentView(true)
        loadingDialog.findViewById<TextView>(R.id.tvMsg).text = "Please wait while we log you in..."
        loadingDialog.show()
        startActivityForResult(signInIntent, REQ_CODE)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                keepUserLogged(1)
                loadingDialog.dismiss()
                navigateToNextScreen()
            } else {
                loadingDialog.dismiss()
                showToast("Request cancelled!", false)
            }

        }
    }

    private fun keepUserLogged(value: Int) {
        requireContext().getSharedPreferences("Logged", Context.MODE_PRIVATE).edit()
            .putInt("isLogged", value).apply()
    }


    companion object {
        private const val REQ_CODE: Int = 10001
    }


    private fun animateParentEnter() {
        binding.apply {
            tLay1.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            tLay2.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            checkboxRemember.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            btnForgotPass.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            btnLogin.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            t2.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
            ivGoogle.apply {
                alpha = 0f
                translationY = 20f
                animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100)
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            }
        }
    }

}
