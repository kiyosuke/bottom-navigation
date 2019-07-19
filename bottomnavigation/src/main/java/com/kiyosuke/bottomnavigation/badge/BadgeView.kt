package com.kiyosuke.bottomnavigation.badge

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.kiyosuke.bottomnavigation.R

class BadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val badgeText: TextView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.notification_badge, this)
        badgeText = root.findViewById(R.id.notificationsBadge)
    }

    fun setNumber(number: Int) {
        badgeText.text = createShowNumber(number)
    }

    private fun createShowNumber(number: Int): String = if (number >= MAX_NUMBER) {
        "$MAX_NUMBER+"
    } else {
        "$number"
    }

    companion object {
        private const val MAX_NUMBER = 99
    }
}