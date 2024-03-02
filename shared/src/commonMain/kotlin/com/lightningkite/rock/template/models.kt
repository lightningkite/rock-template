@file:UseContextualSerialization(Instant::class, UUID::class, ServerFile::class)

package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.now
import com.lightningkite.nowLocal
import com.lightningkite.uuid
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlin.math.roundToInt


@GenerateDataClassPaths
@Serializable
@AdminTableColumns(["email", "role"])
@AdminSearchFields(["email"])
@AdminTitleFields(["email"])
data class User(
    override val _id: UUID = uuid(),
    @Unique override val email: String,
    val phone: String? = null,
    val role: UserRole = UserRole.Customer,
) : HasId<UUID>, HasEmail {
}

@GenerateDataClassPaths
@Serializable
data class FcmToken(
    override val _id: String,
    @Index @References(User::class) val user: UUID,
    @Denormalized val active: Boolean = true,
    val created: Instant = now(),
) : HasId<String>

@GenerateDataClassPaths
@Serializable
data class PublicMessage(
    override val _id: UUID = uuid(),
    @Index @References(User::class) val author: UUID? = null,
    val content: String,
    val at: Instant = now()
): HasId<UUID>

@GenerateDataClassPaths
@Serializable
data class Notification(
    override val _id: UUID = uuid(),
    @References(User::class) val receiver: UUID,
    val title: String,
    val content: String? = null,
    val link: String? = null,
    val at: Instant = now(),
) : HasId<UUID>

@Serializable
enum class UserRole { Anonymous, Customer, Admin, Developer, Root }

annotation class Denormalized

