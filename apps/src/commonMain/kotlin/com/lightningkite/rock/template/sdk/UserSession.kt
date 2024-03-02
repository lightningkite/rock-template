package com.lightningkite.rock.template.sdk

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.db.*
import com.lightningkite.rock.*
import com.lightningkite.rock.navigation.PlatformNavigator
import com.lightningkite.rock.reactive.*
import com.lightningkite.rock.template.*
import com.lightningkite.rock.views.ViewWriter
import com.lightningkite.rock.views.navigator
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlin.time.Duration.Companion.minutes

class UserSession(
    override val api: Api,
    override val userToken: String,
    override val userAccessToken: suspend () -> String,
    val userId: UUID,
) : AbstractUserSession(api, userToken, userAccessToken) {
    val users = ModelCache(user, User.serializer())
    val notifications = ModelCache(notification, Notification.serializer())
    val publicMessages = ModelCache(publicMessage, PublicMessage.serializer())
}

class AnonSession(api: Api): AbstractAnonymousSession(api) {
    val publicMessages = ModelCache(publicMessage, PublicMessage.serializer())
}



@Serializable
enum class ApiOption(val apiName: String, val http: String, val ws: String) {
    Dev("         ", "https://rocktemplateapi.cs.lightningkite.com", "wss://ws.rocktemplateapi.cs.lightningkite.com"),
    Local("Local", "http://localhost:8080", "ws://localhost:8080"),
    SameServer("SameServer", "", "ws://localhost:8080"),
    ;

    companion object {
        var selectedIndex = 0
        fun setSelected(option: ApiOption) {
            selectedIndex = option.ordinal
        }
    }
}

val ApiOption.api get() = LiveApi(http, ws)


inline fun <reified T> Readable<WritableModel<T>>.flatten(): WritableModel<T> {
    return object : WritableModel<T>, Readable<T?> by (shared { this@flatten.await().await() }) {
        override suspend fun set(value: T?) {
            this@flatten.await().set(value)
        }

        override val serializer: KSerializer<T> = serializerOrContextual()

        override suspend fun delete() {
            this@flatten.await().delete()
        }

        override suspend fun modify(modification: Modification<T>): T? {
            return this@flatten.await().modify(modification)
        }
    }
}

inline fun <reified T : HasId<ID>, ID : Comparable<ID>> Readable<CachingModelRestEndpoints<T, ID>>.flatten(): CachingModelRestEndpoints<T, ID> {
    return object : CachingModelRestEndpoints<T, ID> {
        override fun get(id: ID): WritableModel<T> = shared { this@flatten.await().get(id) }.flatten()
        override suspend fun query(query: Query<T>): Readable<List<T>> =
            shared { this@flatten.await().query(query).await() }

        override suspend fun watch(query: Query<T>): Readable<List<T>> =
            shared { this@flatten.await().watch(query).await() }

        override suspend fun insert(item: T): WritableModel<T> =
            shared { this@flatten.awaitRaw().insert(item) }.flatten()

        override suspend fun insert(item: List<T>): List<T> = this@flatten.awaitRaw().insert(item)
        override suspend fun upsert(item: T): WritableModel<T> =
            shared { this@flatten.awaitRaw().upsert(item) }.flatten()

        override suspend fun bulkModify(bulkUpdate: MassModification<T>): Int =
            this@flatten.awaitRaw().bulkModify(bulkUpdate)

        override val skipCache: ModelRestEndpoints<T, ID> get() = TODO()
    }
}


//val currentSession = shared<UserSession?> {
//    val refresh = sessionToken.await() ?: return@shared null
//    val api = selectedApi.await().api
//
//    var lastRefresh: Instant = now()
//    var token: Async<String> = asyncGlobal {
//        try {
//            api.userAuth.getTokenSimple(refresh)
//        } catch(e: LsErrorException) {
//            if (e.status == 400.toShort()) {
//                sessionToken.set(null)
//                PlatformNavigator.navigate(LogInScreen)
//            }
//            throw e
//        }
//    }
//
//    val generateToken = suspend {
//        if (lastRefresh <= now().minus(4.minutes)) {
//            lastRefresh = now()
//            token = asyncGlobal {
//                api.userAuth.getTokenSimple(refresh)
//            }
//        }
//        token.await()
//    }
//
//    val self = api.userAuth.getSelf(generateToken)
//    UserSession(
//        api = api,
//        userToken = refresh,
//        userAccessToken = generateToken,
//        userId = self._id,
//    )
//}
//
//suspend fun ViewWriter.currentSession(): UserSession {
//    val result = currentSession.await()
//    if (result == null) {
//        navigator.navigate(LogInScreen)
//        throw CancelledException()
//    }
//    return result
//}
//
//suspend fun ViewWriter.clearSession() {
//    try {
//        currentSession.await()?.userSession?.terminateSession()
//    } catch (e: Exception) {
//        /*squish*/
//    }
//    sessionToken set null
//}
//
//
//val currentUser = shared { currentSession.awaitNotNull().me }.flatten()
