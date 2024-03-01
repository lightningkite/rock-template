package com.lightningkite.rock.template

import android.app.Application
import android.os.Bundle
import android.widget.FrameLayout
import com.lightningkite.rock.RockActivity
import com.lightningkite.rock.views.AndroidAppContext
import com.lightningkite.rock.views.ViewWriter
import java.io.File

class MainActivity : RockActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeCacheDir.setReadOnly()
        val frame = FrameLayout(this)
        setContentView(frame)
        ViewWriter(frame).app()
    }
}

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
    }
}