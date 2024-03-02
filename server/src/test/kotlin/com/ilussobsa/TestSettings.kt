package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.atZone
import com.lightningkite.lightningdb.get
import com.lightningkite.lightningdb.insertOne
import com.lightningkite.lightningserver.db.DatabaseSettings
import com.lightningkite.lightningserver.email.EmailSettings
import com.lightningkite.lightningserver.engine.UnitTestEngine
import com.lightningkite.lightningserver.engine.engine
import com.lightningkite.lightningserver.logging.LoggingSettings
import com.lightningkite.lightningserver.logging.loggingSettings
import com.lightningkite.lightningserver.notifications.NotificationSettings
import com.lightningkite.lightningserver.settings.Settings
import com.lightningkite.lightningserver.tasks.Tasks
import com.lightningkite.nowLocal
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.random.Random

object TestSettings {
    init {
        Server
        Settings.populateDefaults(
            mapOf(
                Server.database.name to DatabaseSettings("ram"),
                Server.email.name to EmailSettings("test"),
                Server.notifications.name to NotificationSettings("test"),
                loggingSettings.name to LoggingSettings(
                    default = LoggingSettings.ContextSettings(
                        toConsole = true,
                        level = "DEBUG"
                    )
                )
            )
        )
        runBlocking { Tasks.onSettingsReady() }
        engine = UnitTestEngine
        runBlocking { Tasks.onEngineReady() }
    }

}