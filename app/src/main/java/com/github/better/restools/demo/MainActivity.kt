package com.github.better.restools.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.better_activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.better_activity_main)

        btn_test.setOnClickListener {
            text1.setText(R.string.groovy)
            text1.setTextColor(resources.getColor(R.color.better_colorPrimary))
            text1.setBackgroundResource(R.drawable.abc_btn_borderless_material)
            text1.setBackgroundResource(R.drawable.alertdialog_left_selector)
        }
    }
}
