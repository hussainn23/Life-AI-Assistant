package com.example.digitaltrainer.customClasses

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.softec.lifeaiassistant.R


class ErrorToast(context: Context) {

    private val toast: Toast
    private val view: View

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.toast_failure, null)


        toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 20)
        toast.view = view
    }

    fun setText(text: String) {
        val toastText = view.findViewById<TextView>(R.id.toast_text)
        toastText.text = text
    }

    fun show() {
        toast.show()
    }

    private fun dismissToast() {
        toast.cancel()
    }
}
