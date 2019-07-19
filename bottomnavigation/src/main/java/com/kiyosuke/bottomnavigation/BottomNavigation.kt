package com.kiyosuke.bottomnavigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.kiyosuke.bottomnavigation.badge.BadgeView
import kotlin.properties.Delegates

class BottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tabIds = mutableListOf<Int>()
    private var currentSelectedTabId by Delegates.observable<Int?>(null) { _, oldId, newId ->
        if (oldId != newId) {
            changeSelectedTab(oldId, newId)
        }
    }

    var selectedTabPosition: Int?
        get() = currentSelectedTabId?.let { tabId ->
            tabIds.indexOf(tabId)
        }
        set(value) {
            val tabId = value?.let { tabIds.getOrNull(value) }
            currentSelectedTabId = tabId
        }

    private var tabIconTint: Int by Delegates.observable(Color.BLACK) { _, oldColor, newColor ->
        if (oldColor != newColor) {
            children.forEach { view ->
                if (view is Tab) {
                    view.setIconTint(newColor)
                }
            }
        }
    }
    private var tabTextColor: Int by Delegates.observable(Color.BLACK) { _, oldColor, newColor ->
        if (oldColor != newColor) {
            children.forEach { view ->
                if (view is Tab) {
                    view.setTextColor(newColor)
                }
            }
        }
    }

    private var onTabSelectedListener: OnTabSelectedListener? = null

    init {
        orientation = HORIZONTAL
        loadAttrs(attrs)
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ViewCompat.setElevation(this@BottomNavigation, resources.getDimension(R.dimen.bottom_navigation_elevation))
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    gravity = Gravity.CENTER
                }
                this@BottomNavigation.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun loadAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigation)
        tabIconTint = typedArray.getColor(R.styleable.BottomNavigation_tabIconTint, Color.CYAN)
        tabTextColor = typedArray.getColor(R.styleable.BottomNavigation_tabTextColor, Color.BLACK)
        typedArray.recycle()
    }

    fun addTab(@DrawableRes icon: Int, name: String? = null) = apply {
        val tab = createTab(icon, name)
        addTabView(tab)
    }

    fun addTab(@DrawableRes icon: Int, @StringRes resId: Int) = apply {
        addTab(icon, context.getString(resId))
    }

    private fun createTab(icon: Int, name: String? = null): Tab = Tab(context).also { tab ->
        tab.setIcon(icon)
        tab.setText(name)
        tab.setIconTint(tabIconTint)
        tab.setTextColor(tabTextColor)
        val tabId = View.generateViewId()
        tab.id = tabId
        tabIds.add(tabId)
        tab.setOnClickListener {
            currentSelectedTabId = tabId
            onTabSelectedListener?.invoke(tabIds.indexOf(tabId))
        }
    }

    private fun addTabView(tab: Tab) {
        val params = createTabParams()
        this.addView(tab, params)

    }

    private fun createTabParams(): LayoutParams =
        LayoutParams(0, LayoutParams.MATCH_PARENT).also { param ->
            param.weight = 1f
        }

    private fun changeSelectedTab(oldTabId: Int?, tabId: Int?) {
        val oldSelectedTab = findTabById(oldTabId)
        val newSelectedTab = findTabById(tabId)

        oldSelectedTab?.isSelected = false
        newSelectedTab?.isSelected = true

        selectedTabPosition = tabId?.let { id ->
            tabIds.indexOf(id)
        }
    }

    private fun findTabById(id: Int?): Tab? {
        id ?: return null
        return findViewById(id)
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener?) {
        this.onTabSelectedListener = listener
    }

    fun getTab(position: Int): Tab? {
        val tabId = tabIds.getOrNull(position)
        return findTabById(tabId)
    }

    class Tab @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr) {

        private val tabIconImageView: ImageView
        private val tabTextView: TextView
        private val badgeView: BadgeView

        init {
            val root = LayoutInflater.from(context).inflate(R.layout.bottom_tab_view, this)
            tabIconImageView = root.findViewById(R.id.bottom_nav_tab_icon)
            tabTextView = root.findViewById(R.id.bottom_nav_tab_text)
            badgeView = root.findViewById(R.id.badgeView)

            setupViewParams()
        }

        private fun setupViewParams() {
            maxWidth = resources.getDimensionPixelSize(R.dimen.bottom_tab_max_width)
            minWidth = resources.getDimensionPixelSize(R.dimen.bottom_tab_min_width)
        }

        fun setIcon(@DrawableRes icon: Int) {
            tabIconImageView.setImageResource(icon)
            changeMargins()
        }

        fun setText(text: String?) {
            tabTextView.isVisible = !text.isNullOrEmpty()
            tabTextView.text = text ?: ""
            changeMargins()
        }

        fun setText(@StringRes resId: Int) {
            tabTextView.toVisible()
            tabTextView.setText(resId)
            changeMargins()
        }

        fun setTextColor(@ColorInt color: Int) {
            tabTextView.setTextColor(color)
        }

        fun setIconTint(@ColorInt color: Int) {
            tabIconImageView.imageTintList = ColorStateList.valueOf(color)
        }

        fun showBadge(): BadgeView = badgeView.also { badge ->
            badge.toVisible()
        }

        fun hideBadge() {
            badgeView.toGone()
        }

        override fun setSelected(selected: Boolean) {
            super.setSelected(selected)
            val size = if (selected) {
                resources.getDimension(R.dimen.bottom_navigation_selected_text_size)
            } else {
                resources.getDimension(R.dimen.bottom_navigation_text_size)
            }
            tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }

        private fun changeMargins() {
            if (tabTextView.isVisible) {
                // アイコンの上マージンを変更する
                tabIconImageView.updateLayoutParams<LayoutParams> {
                    setMargins(0, resources.getDimensionPixelSize(R.dimen.bottom_tab_with_text_icon_margin), 0, 0)
                }
            } else {
                tabIconImageView.updateLayoutParams<LayoutParams> {
                    setMargins(0, 0, 0, 0)
                }
            }
        }
    }
}

private typealias OnTabSelectedListener = (position: Int) -> Unit

private fun View.toVisible() {
    this.visibility = View.VISIBLE
}

private fun View.toGone() {
    this.visibility = View.GONE
}