package com.lightningkite.rock.template.sdk

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.LSError
import com.lightningkite.lightningserver.StringArrayFormat
import com.lightningkite.rock.*
import com.lightningkite.rock.reactive.Readable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val json = Json {
    serializersModule = ClientModule
    ignoreUnknownKeys = true
}
val stringArrayFormat = StringArrayFormat(ClientModule)
inline fun <reified T> T.urlify(): String = stringArrayFormat.encodeToString(this)

suspend inline fun <reified T> RequestResponse.readJson() = json.decodeFromString<T>(text())
suspend fun RequestResponse.discard() = Unit
suspend inline fun <reified T> T.toJsonRequestBody() = json.encodeToString(this)

class LsErrorException(val status: Short, val error: LSError): IllegalStateException(
    message = error.message
)

object HttpClient {
    val GET: HttpMethod = HttpMethod.GET
    val POST: HttpMethod = HttpMethod.POST
    val PATCH: HttpMethod = HttpMethod.PATCH
    val DELETE: HttpMethod = HttpMethod.DELETE
    val PUT: HttpMethod = HttpMethod.PUT
}

suspend inline fun <reified IN, reified OUT> fetch(
    url: String,
    method: HttpMethod = HttpMethod.GET,
    noinline token: (suspend () ->String)? = null,
    headers: HttpHeaders = httpHeaders(),
    body: IN
): OUT = com.lightningkite.rock.fetch(
    url = url,
    method = method,
    headers = headers.apply {
        token?.let { append("Authorization", "Bearer ${it()}") }
    },
    body = RequestBodyText(json.encodeToString(body), "application/json")
).let { it: RequestResponse ->
    try {
        if (it.ok && OUT::class == Unit::class) Unit as OUT
        else if (it.ok) json.decodeFromString(it.text())
        else {
            throw LsErrorException(it.status, json.decodeFromString(it.text()))
        }
    } catch (e:Exception){
        e.printStackTrace()
        throw e
    }
}

suspend inline fun <reified OUT> fetch(
    url: String,
    method: HttpMethod = HttpMethod.GET,
    noinline token: (suspend () -> String)? = null,
    headers: HttpHeaders = httpHeaders()
): OUT = com.lightningkite.rock.fetch(
    url = url,
    method = method,
    headers = headers.apply {
        append("Content-Type", "application/json")
        token?.let { append("Authorization", "Bearer ${it()}") }
    },
    body = null
).let {
    if (it.ok && OUT::class == Unit::class) Unit as OUT
    else if (it.ok) json.decodeFromString(it.text())
    else {
        throw LsErrorException(it.status, json.decodeFromString(it.text()))
    }
}

suspend inline fun <reified IN, reified OUT> multiplexedSocket(
    socketUrl: String,
    path: String,
    token: String?,
): TypedWebSocket<IN, OUT> = multiplexSocket(
    url = "$socketUrl/?path=multiplex${token?.let { "?jwt=${encodeURIComponent(it)}" } ?: ""}",
    path = path,
    params = emptyMap(),
    json = json
).typed(json, serializerOrContextual(), serializerOrContextual())


class ListReadable<MODEL : HasId<UUID>>(val wraps: Readable<ListChange<MODEL>?>) : Readable<List<MODEL>> {
    val localList = ArrayList<MODEL>()

    override fun addListener(listener: () -> Unit): () -> Unit {
        return wraps.addListener(listener)
    }

    override suspend fun awaitRaw(): List<MODEL> {
        val basis: ListChange<MODEL> = wraps.awaitRaw() ?: ListChange()

        basis.wholeList?.let { localList.clear(); localList.addAll(it) }
        basis.new?.let {
            basis.old?.let{
                localList.removeAll { o -> it._id == o._id }
            }
            localList.add(it)
        }
            ?: basis.old?.let { localList.removeAll { o -> it._id == o._id } }

        return localList
    }


    override fun hashCode(): Int = wraps.hashCode() + 1

    override fun equals(other: Any?): Boolean = other is ListReadable<*> && this.wraps == other.wraps
}

fun <MODEL : HasId<UUID>> Readable<ListChange<MODEL>?>.toListReadable(): Readable<List<MODEL>> = ListReadable(this)


class SingleUpdates<MODEL : HasId<UUID>>(val wraps: Readable<ListChange<MODEL>?>) : Readable<EntryChange<MODEL>> {

    override fun addListener(listener: () -> Unit): () -> Unit {
        return wraps.addListener(listener)
    }

    override suspend fun awaitRaw(): EntryChange<MODEL> {
        val incoming: ListChange<MODEL> = wraps.awaitRaw() ?: ListChange()

        return incoming.wholeList?.firstOrNull()?.let {
            EntryChange(null, it)
        }
            ?: incoming.new?.let {
                EntryChange(incoming.old, it)
            }
            ?: incoming.old?.let {
                EntryChange(it, null)
            }
            ?: EntryChange(null, null)
    }


    override fun hashCode(): Int = wraps.hashCode() + 1

    override fun equals(other: Any?): Boolean = other is SingleUpdates<*> && this.wraps == other.wraps
}


fun <MODEL : HasId<UUID>> Readable<ListChange<MODEL>?>.toSingleUpdates(): Readable<EntryChange<MODEL>> = SingleUpdates(this)

/*
    val localList = ArrayList<T>()
    return map {
        it.wholeList?.let { localList.clear(); localList.addAll(it.sortedWith(ordering)) }
        it.new?.let {
            localList.removeAll { o -> it._id == o._id }
            var index = localList.indexOfFirst { inList -> ordering.compare(it, inList) < 0 }
            if (index == -1) index = localList.size
            localList.add(index, it)
        } ?: it.old?.let { localList.removeAll { o -> it._id == o._id } }
        localList
    }
}
 */