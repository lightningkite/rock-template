package com.lightningkite.rock.template

import com.lightningkite.GeoCoordinate
import com.lightningkite.kotlinercli.cli
import com.lightningkite.lightningserver.aws.terraformAws
import com.lightningkite.lightningserver.cache.LocalCache
import com.lightningkite.lightningserver.engine.LocalEngine
import com.lightningkite.lightningserver.engine.engine
import com.lightningkite.lightningserver.ktor.runServer
import com.lightningkite.lightningserver.pubsub.LocalPubSub
import com.lightningkite.lightningserver.serialization.Serialization
import com.lightningkite.lightningserver.settings.loadSettings
import com.lightningkite.lightningserver.typed.Documentable
import com.lightningkite.lightningserver.typed.kotlinSdkLocal
import kotlinx.coroutines.runBlocking
import java.io.File

fun setup() {
    Server
}
fun serve() {
    setup2
    assert(Serialization.json.serializersModule.getContextual(GeoCoordinate::class) != null)
    runServer(LocalPubSub, LocalCache)
}
fun terraform() {
    terraformAws("com.lightningkite.rock.template.AwsHandler", "ilussobsa", File("server/terraform"))
}
private val setup2 by lazy {
    loadSettings(File("settings.json"))
}

fun serverCheck() = runBlocking {
    setup2
    engine = LocalEngine(LocalCache)
}

fun sdk() {
    Documentable.kotlinSdkLocal("com.lightningkite.rock.template.sdk", File("apps/src/commonMain/kotlin/com/lightningkite/rock/template/sdk"))
}

fun main(vararg args: String) = cli(
    args,
    ::setup,
    listOf(
        ::serve,
        ::terraform,
        ::serverCheck,
        ::sdk,
    )
)

