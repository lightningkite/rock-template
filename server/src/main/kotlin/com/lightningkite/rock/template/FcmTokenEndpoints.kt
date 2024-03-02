package com.lightningkite.rock.template

import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.auth.authOptions
import com.lightningkite.lightningserver.auth.id
import com.lightningkite.lightningserver.core.ServerPath
import com.lightningkite.lightningserver.core.ServerPathGroup
import com.lightningkite.lightningserver.db.ModelRestEndpoints
import com.lightningkite.lightningserver.typed.auth
import java.util.*


class FcmTokenEndpoints(path: ServerPath) : ServerPathGroup(path) {
    val info = FcmToken::class.modelInfoWithDefault(
        database = Server.database,
        authOptions = authOptions<User>(),
        permissions = {
            val admin = condition<FcmToken>(auth.role() >= UserRole.Admin)
            val owner = condition<FcmToken> { it.user.eq(auth.id) }
            val anyone = condition<FcmToken>(true)
            ModelPermissions(
                create = anyone,
                update = anyone,
                updateRestrictions = updateRestrictions {
                    it.user.requires(anyone) { it.eq(auth.id) }
                },
                read = admin or owner,
                delete = admin or owner,
            )
        },
        defaultItem = { FcmToken(_id = "", user = UUID(0L, 0L)) },
    )
    val rest = ModelRestEndpoints(path, info)
}
