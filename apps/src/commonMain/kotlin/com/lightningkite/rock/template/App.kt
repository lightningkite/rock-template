package com.lightningkite.rock.template

import com.lightningkite.rock.CancelledException
import com.lightningkite.rock.Routable
import com.lightningkite.rock.contains
import com.lightningkite.rock.models.*
import com.lightningkite.rock.navigation.*
import com.lightningkite.rock.reactive.*
import com.lightningkite.rock.views.*
import com.lightningkite.rock.views.direct.*
import com.lightningkite.rock.views.l2.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties


val appTheme = MaterialLikeTheme(
    primary = Color.fromHexString("#0014CC"),
    elevation = 0.rem,
    outlineWidth = 2.dp,
    background = Color.white,
)

fun ViewWriter.app() {
    appBase(AutoRoutes) {
        navigatorView(navigator)
    }
}

@Routable("/")
class LandingScreen() : RockScreen {
    override fun ViewWriter.render() {
        col {
            h1("Hello world!")
            text("Welcome to Rock!")
        }
    }
}

