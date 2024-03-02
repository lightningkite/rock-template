package com.lightningkite.rock.template

import com.lightningkite.rock.launchGlobal
import com.lightningkite.rock.navigation.PlatformNavigator
import com.lightningkite.rock.template.sdk.ApiOption
import com.lightningkite.rock.views.ViewWriter
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    // If hosted not at the base,
    if(PlatformNavigator.basePath != "/") {
        // default the API to same site
        launchGlobal {
            selectedApi set ApiOption.SameServer
        }
    }
    window.asDynamic().setFcmToken = setFcmToken
    val context = ViewWriter(document.body!!)
    context.app()
}
