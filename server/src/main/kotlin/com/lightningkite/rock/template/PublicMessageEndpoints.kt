package com.lightningkite.rock.template

import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.auth.authOptions
import com.lightningkite.lightningserver.auth.id
import com.lightningkite.lightningserver.core.ServerPath
import com.lightningkite.lightningserver.core.ServerPathGroup
import com.lightningkite.lightningserver.db.ModelRestEndpoints
import com.lightningkite.lightningserver.db.restApiWebsocket
import com.lightningkite.lightningserver.email.Email
import com.lightningkite.lightningserver.email.EmailLabeledValue
import com.lightningkite.lightningserver.email.EmailPersonalization
import com.lightningkite.lightningserver.email.emailPlainTextToHtml
import com.lightningkite.lightningserver.settings.generalSettings
import com.lightningkite.lightningserver.typed.auth
import com.lightningkite.now
import com.lightningkite.uuid
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

class PublicMessageEndpoints(path: ServerPath) : ServerPathGroup(path) {
    val info = PublicMessage::class.modelInfoWithDefault(
        database = Server.database,
        authOptions = authOptions<User?>(),
        permissions = {
            val admin = condition<PublicMessage>(user()?.role?.let { it >= UserRole.Admin } == true)
            val root = condition<PublicMessage>(user()?.role?.let { it >= UserRole.Root } == true)
            val author = condition<PublicMessage> { it.author inside setOf(authOrNull?.id, null) }
            ModelPermissions(
                create = author or admin,
                update = author or admin,
                read = condition(true),
                delete = author or admin,
            )
        },
        defaultItem = { PublicMessage(author = uuid(), content = "") }
    )
    val rest = ModelRestEndpoints(path, info)
    val restSocket = path("rest").restApiWebsocket(Server.database, info)
}

