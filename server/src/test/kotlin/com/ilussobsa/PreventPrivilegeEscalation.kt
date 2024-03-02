package com.lightningkite.rock.template

import com.lightningkite.lightningdb.insertOne
import com.lightningkite.lightningdb.modification
import com.lightningkite.lightningserver.exceptions.NotFoundException
import com.lightningkite.lightningserver.notifications.FcmNotificationClient
import com.lightningkite.lightningserver.typed.test
import com.lightningkite.uuid
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.fail

class PreventPrivilegeEscalation {
    @Test
    fun test(): Unit = runBlocking {
        TestSettings
        val nonAdmin = Server.users.info.collection().insertOne(User(email = "${uuid()}@test.com"))!!
        for (role in setOf(UserRole.Developer, UserRole.Root, UserRole.Admin)) {
            assertThrows<NotFoundException> {
                Server.users.rest.modify.test(nonAdmin, nonAdmin._id, modification {
                    it.role assign UserRole.Root
                })
            }
        }
        val admin =
            Server.users.info.collection().insertOne(User(email = "${uuid()}@test.com", role = UserRole.Admin))!!
        for (role in setOf(UserRole.Developer, UserRole.Root)) {
            assertThrows<NotFoundException> {
                Server.users.rest.modify.test(admin, admin._id, modification {
                    it.role assign UserRole.Root
                })
            }
        }
    }
}

inline fun <reified T: Throwable> assertThrows(action: ()->Unit) {
    try {
        action()
        fail()
    } catch(t: Throwable) {
        assert(t is T)
    }
}