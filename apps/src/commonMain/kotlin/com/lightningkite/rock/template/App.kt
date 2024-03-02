package com.lightningkite.rock.template

import com.lightningkite.lightningdb.Query
import com.lightningkite.lightningdb.sort
import com.lightningkite.rock.CancelledException
import com.lightningkite.rock.Routable
import com.lightningkite.rock.contains
import com.lightningkite.rock.locale.RenderSize
import com.lightningkite.rock.locale.renderToString
import com.lightningkite.rock.models.*
import com.lightningkite.rock.navigation.*
import com.lightningkite.rock.reactive.*
import com.lightningkite.rock.template.sdk.*
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
    prepareModels()
    appBase(AutoRoutes) {
        navigatorView(navigator)
    }
}

val fcmToken: Property<String?> = Property(null)
val setFcmToken = { token: String -> fcmToken.value = token }
val selectedApi = PersistentProperty<ApiOption>("apiOption", ApiOption.Dev)

@Routable("/")
class LandingScreen() : RockScreen {
    override fun ViewWriter.render() {
        col {
            val thing = shared { AnonSession(selectedApi.await().api).publicMessages.watch(Query(orderBy = sort { it.at.descending() })) }
            expanding - recyclerView {
                children(shared {
                    thing.await().await()
                }) {
                    card - col {
                        text { ::content { it.await().content }}
                        subtext { ::content { it.await().at.renderToString(RenderSize.Abbreviation) }}
                    }
                }
            }
            row {
                val toSend = Property("")
                expanding - textField {
                    content bind toSend
                }
                button {
                    icon { source = Icon.send }
                    onClick {
                        AnonSession(selectedApi.await().api).publicMessages.insert(PublicMessage(
                            content = toSend.await()
                        ))
                        toSend.set("")
                    }
                }
            }
        }
    }
}

