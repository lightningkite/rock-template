package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.lightningserver.auth.RequestAuth
import com.lightningkite.lightningserver.typed.AuthAccessor
import com.lightningkite.lightningserver.typed.auth
import kotlinx.serialization.KSerializer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object RoleCacheKey : RequestAuth.CacheKey<User, UUID, UserRole>() {
    override val name: String
        get() = "role"
    override val serializer: KSerializer<UserRole>
        get() = UserRole.serializer()
    override val validFor: Duration
        get() = 5.minutes

    override suspend fun calculate(auth: RequestAuth<User>): UserRole = auth.get().role
}

suspend fun RequestAuth<User>.role() = this.get(RoleCacheKey)
suspend fun AuthAccessor<User>.role() = this.auth.get(RoleCacheKey)
@JvmName("role2") suspend fun AuthAccessor<User?>.role() = this.authOrNull?.get(RoleCacheKey) ?: UserRole.Customer