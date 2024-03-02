package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.auth.authOptions
import com.lightningkite.lightningserver.auth.id
import com.lightningkite.lightningserver.auth.user
import com.lightningkite.lightningserver.core.ServerPath
import com.lightningkite.lightningserver.core.ServerPathGroup
import com.lightningkite.lightningserver.db.ModelRestEndpoints
import com.lightningkite.lightningserver.db.ModelSerializationInfo
import com.lightningkite.lightningserver.db.modelInfoWithDefault
import com.lightningkite.lightningserver.db.restApiWebsocket
import com.lightningkite.lightningserver.email.Email
import com.lightningkite.lightningserver.email.EmailLabeledValue
import com.lightningkite.lightningserver.exceptions.report
import com.lightningkite.lightningserver.http.HttpResponse
import com.lightningkite.lightningserver.http.get
import com.lightningkite.lightningserver.http.handler
import com.lightningkite.lightningserver.schedule.schedule
import com.lightningkite.lightningserver.tasks.startupOnce
import com.lightningkite.lightningserver.typed.auth
import com.lightningkite.now
import com.lightningkite.nowLocal
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration.Companion.days

class UserEndpoints(path: ServerPath) : ServerPathGroup(path) {
    val info = modelInfoWithDefault(
        serialization = ModelSerializationInfo<User, UUID>(),
        authOptions = authOptions<User>(),
        getBaseCollection = { Server.database().collection<User>() },
        forUser = {
            val admin: Condition<User> =
                if (this.auth.role() >= UserRole.Admin) Condition.Always() else Condition.Never()
            val self = condition<User> { it._id eq auth.id }
            val public = condition<User>(true)
            it.withPermissions(
                ModelPermissions(
                    create = admin,
                    read = public,
                    update = admin or self,
                    updateRestrictions = updateRestrictions {
                        it.role.requires(admin) { it.inside(UserRole.values().filter { it <= auth.role() }) }
                    },
                    delete = admin or self,
                )
            )
        },
        defaultItem = { User(email = "") },
        exampleItem = { User(email = "example@test.com") }
    )
    val rest = ModelRestEndpoints(path("rest"), info)
    val restSocket = path("rest").restApiWebsocket(Server.database, info)

    init {
        startupOnce("initAdminUser", Server.database) {
            val item = User(
                _id = UUID.fromString("43e581d3-46e2-4fd3-8ccb-e846dfdc90af"),
                email = "joseph+admin@lightningkite.com",
                role = UserRole.Root
            )
            info.collection().upsertOneById(
                item._id,
                item
            )
        }
    }

}