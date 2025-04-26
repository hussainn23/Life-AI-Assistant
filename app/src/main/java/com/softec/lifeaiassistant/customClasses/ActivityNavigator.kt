package com.softec.lifeaiassistant.customClasses

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat

class ActivityNavigator {

    companion object {
        fun startActivity(
            from: Activity,
            to: Class<out Activity>,
            sharedView: View? = null,
            clearStack: Boolean = false,
            key: String? = null,
            value: String? = null
        ) {
            val intent = Intent(from, to)
            if (clearStack) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            if (key != null && value != null) {
                intent.putExtra(key, value)
            }

            sharedView?.let {
                val transitionName = ViewCompat.getTransitionName(it)
                if (!transitionName.isNullOrEmpty()) {
                    val options = ActivityOptionsCompat.makeScaleUpAnimation(
                        it,  // The view to scale from
                        0,  // X start position
                        0,  // Y start position
                        it.width,   // Initial width
                        it.height   // Initial height
                    )
                    from.startActivity(intent, options.toBundle())
                    return
                }
            }
            Log.e("ActivityNavigator", "Starting activity without shared element transition")
            from.startActivity(intent)
        }
    }
}
