package com.ljmu.andre.snaptools.Dialogs.Content

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        MyActivityUI().setContentView(this)
    }
}

class MyActivityUI : AnkoComponent<MyActivity> {
    override fun createView(ui: AnkoContext<MyActivity>) = ui.apply {
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                setOnClickListener { ctx.toast("Hello, ${name.text}!") }
            }
        }
    }.view
}