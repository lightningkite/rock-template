package com.lightningkite.rock.template

import com.lightningkite.UUID
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
import com.lightningkite.lightningserver.notifications.NotificationSendResult
import com.lightningkite.lightningserver.settings.generalSettings
import com.lightningkite.lightningserver.typed.auth
import com.lightningkite.now
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

class NotificationEndpoints(path: ServerPath) : ServerPathGroup(path) {
    val info = Notification::class.modelInfoWithDefault(
        database = Server.database,
        authOptions = authOptions<User>(),
        permissions = {
            val admin = condition<Notification>(user()?.role?.let { it >= UserRole.Admin } == true)
            val receiver = condition<Notification> { it.receiver.eq(this.auth.id) }
            ModelPermissions(
                create = admin,
                update = admin,
                read = receiver,
                delete = admin,
            )
        },
        defaultItem = { Notification(receiver = UUID(0L, 0L), title = "", content = "") },
        modifyCoreCollection = {
            it.postCreateBulk { notifications ->
                val relatedFcms = Server.fcmTokens.info.collection().find(condition {
                    it.user inside notifications.map { it.receiver }.distinct()
                }).toList()
                val tokensToRemove = HashSet<String>()
                notifications.groupBy { it.copy(_id = UUID(0L, 0L), receiver = UUID(0L, 0L), at = Instant.DISTANT_PAST) }
                    .forEach { (common, to) ->
                        val users = to.map { it.receiver }.distinct()
                        relatedFcms.asSequence()
                            .filter { it.active }
                            .filter { it.user in users }
                            .map { it._id }
                            .toList()
                            .takeUnless { it.isEmpty() }
                            ?.let {
                                tokensToRemove += Server.notifications().send(
                                    targets = it,
                                    title = common.title,
                                    body = common.content,
                                    link = common.link,
                                ).also {
                                    it.forEach { println("token ${it.key}: ${it.value}") }
                                }.filterValues { it == NotificationSendResult.DeadToken }.keys
                            }
                    }
                val dayAgo = now() - 1.days
                tokensToRemove.removeAll {
                    relatedFcms.find { f -> f._id == it }?.created?.let { it < dayAgo } ?: false
                }
                if (tokensToRemove.isNotEmpty()) {
                    Server.fcmTokens.info.collection().deleteMany(condition {
                        it._id inside tokensToRemove
                    })
                }
            }
        }
    )
    val rest = ModelRestEndpoints(path, info)
    val restSocket = path("rest").restApiWebsocket(Server.database, info)
}

//http://services.chromedata.com/Description/7c?wsdl