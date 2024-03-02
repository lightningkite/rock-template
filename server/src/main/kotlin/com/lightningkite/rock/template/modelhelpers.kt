package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.auth.AuthOptions
import com.lightningkite.lightningserver.auth.authOptions
import com.lightningkite.lightningserver.auth.id
import com.lightningkite.lightningserver.db.ModelSerializationInfo
import com.lightningkite.lightningserver.db.modelInfoWithDefault
import com.lightningkite.lightningserver.pascalCase
import com.lightningkite.lightningserver.typed.AuthAccessor
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


suspend fun <T> AuthAccessor<User?>.publicRead() = ModelPermissions<T>(
    manage = condition(role() >= UserRole.Admin),
    read = condition(true)
)

fun <T> dangerousPublic() = ModelPermissions<T>(
    manage = condition(true),
    read = condition(true)
)

suspend fun <T> AuthAccessor<User>.adminOnly() = ModelPermissions<T>(
    all = condition(role() >= UserRole.Admin)
)

suspend inline fun <reified T> AuthAccessor<User>.owned(ownerPath: (DataClassPath<T, T>) -> DataClassPath<T, UUID>) =
    ModelPermissions<T>(
        all = condition<T>(role() >= UserRole.Admin) or condition<T> { ownerPath(it) eqNn authOrNull?.id }
    )

inline fun <reified T : HasId<ID>, reified ID : Comparable<ID>> KClass<T>.modelInfoWithDefaultPublicRead(
    noinline modifyCoreCollection: (FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline modifyUserCollection: AuthAccessor<User?>.(FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline defaultItem: () -> T,
) = modelInfoWithDefault(
    database = Server.database,
    authOptions = authOptions<User?>(),
    permissions = AuthAccessor<User?>::publicRead,
    defaultItem = defaultItem,
    modifyCoreCollection = modifyCoreCollection,
    modifyUserCollection = modifyUserCollection,
)

inline fun <reified T : HasId<ID>, reified ID : Comparable<ID>> KClass<T>.modelInfoWithDefaultAdminOnly(
    noinline modifyCoreCollection: (FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline modifyUserCollection: AuthAccessor<User>.(FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline defaultItem: () -> T,
) = modelInfoWithDefault(
    database = Server.database,
    authOptions = authOptions<User>(),
    permissions = AuthAccessor<User>::adminOnly,
    defaultItem = defaultItem,
    modifyCoreCollection = modifyCoreCollection,
    modifyUserCollection = modifyUserCollection,
)

inline fun <reified T : HasId<ID>, reified ID : Comparable<ID>> KClass<T>.modelInfoWithDefaultOwned(
    crossinline ownershipPath: (DataClassPath<T, T>) -> DataClassPath<T, UUID>,
    noinline modifyCoreCollection: (FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline modifyUserCollection: AuthAccessor<User>.(FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline defaultItem: () -> T,
) = modelInfoWithDefault(
    database = Server.database,
    authOptions = authOptions<User>(),
    permissions = { owned(ownershipPath) },
    defaultItem = defaultItem,
    modifyCoreCollection = modifyCoreCollection,
    modifyUserCollection = modifyUserCollection,
)

fun iWillUse(vararg stuff: Any) = Unit

inline fun <reified USER : HasId<*>?, reified T : HasId<ID>, reified ID : Comparable<ID>> KClass<T>.modelInfoWithDefault(
    crossinline database: () -> Database,
    authOptions: AuthOptions<USER>,
    noinline permissions: suspend AuthAccessor<USER>.() -> ModelPermissions<T>,
    crossinline defaultItem: () -> T,
    noinline modifyCoreCollection: (FieldCollection<T>) -> FieldCollection<T> = { it },
    crossinline modifyUserCollection: AuthAccessor<USER>.(FieldCollection<T>) -> FieldCollection<T> = { it },
) = com.lightningkite.lightningserver.db.modelInfoWithDefault<USER, T, ID>(
    serialization = ModelSerializationInfo<T, ID>(),
    authOptions = authOptions,
    getBaseCollection = {
        database().collection(
            serializerOrContextual<T>(),
            serializerOrContextual<T>().descriptor.serialName.substringAfterLast('.').pascalCase()
        )
    },
    getCollection = modifyCoreCollection,
    forUser = { modifyUserCollection(it.withPermissions(permissions())) },
    defaultItem = { defaultItem() },
    exampleItem = { defaultItem() },
)

fun <Model : Any> FieldCollection<Model>.postCreateBulk(
    onCreate: suspend (List<Model>) -> Unit
): FieldCollection<Model> = object : FieldCollection<Model> by this@postCreateBulk {
    override val wraps = this@postCreateBulk
    override suspend fun insert(models: Iterable<Model>): List<Model> {
        val result = wraps.insertMany(models)
        onCreate(result)
        return result
    }

    override suspend fun upsertOne(
        condition: Condition<Model>,
        modification: Modification<Model>,
        model: Model
    ): EntryChange<Model> = wraps.upsertOne(condition, modification, model).also {
        if (it.old == null) onCreate(listOf(it.new!!))
    }
}


/**
 * Intercepts all changes sent to the database, including inserting, replacing, upserting, and updating.
 * Also gives you an instance of the model that will be changed.
 * This is significantly more expensive, as we must retrieve the data before we can calculate the change.
 */
inline fun <Model : HasId<ID>, ID : Comparable<ID>> FieldCollection<Model>.interceptChangesPerInstance(
    includeMassUpdates: Boolean = true,
    crossinline interceptors: suspend (List<Pair<Model, Modification<Model>>>) -> List<Modification<Model>>
): FieldCollection<Model> =
    object : FieldCollection<Model> by this {
        override val wraps = this@interceptChangesPerInstance
        override suspend fun insert(models: Iterable<Model>): List<Model> =
            wraps.insert(interceptors(models.map { it to Modification.Assign(it) }).zip(models) { a, b -> a(b) })

        suspend fun interceptor(model: Model, modification: Modification<Model>): Modification<Model> =
            interceptors(listOf(model to modification))[0]

        override suspend fun replaceOne(
            condition: Condition<Model>,
            model: Model,
            orderBy: List<SortPart<Model>>
        ): EntryChange<Model> {
            val current = wraps.findOne(condition) ?: return EntryChange(null, null)
            return wraps.replaceOne(
                Condition.OnField(serializer._id(), Condition.Equal(current._id)),
                interceptor(current, Modification.Assign(model))(model),
                orderBy
            )
        }

        override suspend fun replaceOneIgnoringResult(
            condition: Condition<Model>,
            model: Model,
            orderBy: List<SortPart<Model>>
        ): Boolean {
            val current = wraps.findOne(condition) ?: return false
            return wraps.replaceOneIgnoringResult(
                Condition.OnField(serializer._id(), Condition.Equal(current._id)),
                interceptor(current, Modification.Assign(model))(model),
                orderBy
            )
        }

        override suspend fun upsertOne(
            condition: Condition<Model>,
            modification: Modification<Model>,
            model: Model
        ): EntryChange<Model> {
            val current = wraps.findOne(condition) ?: return wraps.upsertOne(condition, modification, model)
            val changed = interceptor(current, modification)
            return wraps.upsertOne(
                condition and Condition.OnField(serializer._id(), Condition.Equal(current._id)),
                changed,
                changed(model)
            )
        }

        override suspend fun upsertOneIgnoringResult(
            condition: Condition<Model>,
            modification: Modification<Model>,
            model: Model
        ): Boolean {
            val current =
                wraps.findOne(condition) ?: return wraps.upsertOneIgnoringResult(condition, modification, model)
            val changed = interceptor(current, modification)
            return wraps.upsertOneIgnoringResult(
                condition and Condition.OnField(
                    serializer._id(),
                    Condition.Equal(current._id)
                ), changed, changed(model)
            )
        }

        override suspend fun updateOne(
            condition: Condition<Model>,
            modification: Modification<Model>,
            orderBy: List<SortPart<Model>>
        ): EntryChange<Model> {
            val current = wraps.findOne(condition) ?: return EntryChange(null, null)
            return wraps.updateOne(
                Condition.OnField(serializer._id(), Condition.Equal(current._id)),
                interceptor(current, modification),
                orderBy
            )
        }

        override suspend fun updateOneIgnoringResult(
            condition: Condition<Model>,
            modification: Modification<Model>,
            orderBy: List<SortPart<Model>>
        ): Boolean {
            val current = wraps.findOne(condition) ?: return false
            return wraps.updateOneIgnoringResult(
                Condition.OnField(serializer._id(), Condition.Equal(current._id)),
                interceptor(current, modification),
                orderBy
            )
        }

        override suspend fun updateMany(
            condition: Condition<Model>,
            modification: Modification<Model>
        ): CollectionChanges<Model> {
            if (!includeMassUpdates) return wraps.updateMany(condition, modification)
            val all = ArrayList<EntryChange<Model>>()
            val field = serializer._id()
            wraps.find(condition).collectChunked(100) {
                val altMods = interceptors(it.map { it to modification })
                altMods.zip(it) { altMod, model ->
                    val id = field.get(model)
                    all.add(wraps.updateOne(DataClassPathSelf(serializer).get(field).eq(id), altMod))
                }
            }
            return CollectionChanges(all)
        }

        override suspend fun updateManyIgnoringResult(
            condition: Condition<Model>,
            modification: Modification<Model>
        ): Int {
            if (!includeMassUpdates) return wraps.updateManyIgnoringResult(condition, modification)
            var count = 0
            val field = serializer._id()
            wraps.find(condition).collectChunked(100) {
                val altMods = interceptors(it.map { it to modification })
                altMods.zip(it) { altMod, model ->
                    val id = field.get(model)
                    if (wraps.updateOneIgnoringResult(DataClassPathSelf(serializer).get(field).eq(id), altMod)) count++
                }
            }
            return count
        }
    }

fun <Model : HasId<ID>, ID : Comparable<ID>> FieldCollection<Model>.postNewValues(
    changedBulk: suspend (List<Model>) -> Unit
): FieldCollection<Model> = object : FieldCollection<Model> by this@postNewValues {
    override val wraps = this@postNewValues

    suspend fun changed(model: Model) = changedBulk(listOf(model))

    override suspend fun insert(models: Iterable<Model>): List<Model> {
        return wraps.insert(models).also { changedBulk(it) }
    }

    override suspend fun replaceOne(
        condition: Condition<Model>,
        model: Model,
        orderBy: List<SortPart<Model>>
    ): EntryChange<Model> =
        wraps.replaceOne(condition, model, orderBy).also { if (it.old != null && it.new != null) changed(it.new!!) }

    override suspend fun upsertOne(
        condition: Condition<Model>,
        modification: Modification<Model>,
        model: Model
    ): EntryChange<Model> =
        wraps.upsertOne(condition, modification, model).also { it.new?.let { changed(it) } }

    override suspend fun updateOne(
        condition: Condition<Model>,
        modification: Modification<Model>,
        orderBy: List<SortPart<Model>>
    ): EntryChange<Model> =
        wraps.updateOne(condition, modification, orderBy)
            .also { if (it.old != null && it.new != null) changed(it.new!!) }

    override suspend fun updateMany(
        condition: Condition<Model>,
        modification: Modification<Model>
    ): CollectionChanges<Model> = wraps.updateMany(condition, modification).also { changes ->
        changedBulk(changes.changes.mapNotNull { it.new })
    }

    override suspend fun replaceOneIgnoringResult(
        condition: Condition<Model>,
        model: Model,
        orderBy: List<SortPart<Model>>
    ): Boolean = replaceOne(
        condition,
        model
    ).new != null

    override suspend fun upsertOneIgnoringResult(
        condition: Condition<Model>,
        modification: Modification<Model>,
        model: Model
    ): Boolean = upsertOne(condition, modification, model).old != null

    override suspend fun updateOneIgnoringResult(
        condition: Condition<Model>,
        modification: Modification<Model>,
        orderBy: List<SortPart<Model>>
    ): Boolean = updateOne(condition, modification).new != null

    override suspend fun updateManyIgnoringResult(condition: Condition<Model>, modification: Modification<Model>): Int =
        updateMany(condition, modification).changes.size
}