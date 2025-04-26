package com.softec.lifeaiassistant.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.softec.lifeaiassistant.fragments.Login
import com.softec.lifeaiassistant.fragments.Signup

class AuthenticationPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val tabTitles = listOf("Login", "Signup")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Login()
            1 -> Signup()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return tabTitles.size
    }
}