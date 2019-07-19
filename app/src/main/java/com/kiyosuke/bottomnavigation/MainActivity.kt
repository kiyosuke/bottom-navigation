package com.kiyosuke.bottomnavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation
            .addTab(R.drawable.ic_home, "ホーム")
            .addTab(R.drawable.ic_message, "メッセージ")
            .addTab(R.drawable.ic_people, "フレンド")
            .addTab(R.drawable.ic_shopping_cart, "買い物")
            .addTab(R.drawable.ic_train, "時刻表")
            .addTab(R.drawable.ic_map, "地図")

        bottomNavigation.setOnTabSelectedListener { position ->
            Log.d(LOG_TAG, "tabSelected: $position")
        }

        bottomNavigation.getTab(1)?.showBadge()?.setNumber(5)
    }

    companion object {
        private const val LOG_TAG = "MainActivity"
    }
}
