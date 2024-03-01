package com.lightningkite.rock.template

import com.lightningkite.rock.launchGlobal
import com.lightningkite.rock.navigation.PlatformNavigator
import com.lightningkite.rock.views.ViewWriter
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    val context = ViewWriter(document.body!!)
    context.app()
}
