@file:SharedCode
package com.lightningkite.rock.template.sdk

import com.lightningkite.khrysalis.SharedCode
import com.lightningkite.*
import com.lightningkite.rock.*
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.db.*
import kotlinx.datetime.*
import com.lightningkite.lightningserver.auth.oauth.*
import com.lightningkite.lightningserver.auth.proof.*
import com.lightningkite.lightningserver.auth.subject.*
import kotlin.String
import com.lightningkite.lightningserver.files.UploadInformation
import com.lightningkite.lightningserver.serverhealth.ServerHealth
import com.lightningkite.lightningserver.typed.BulkRequest
import com.lightningkite.lightningserver.typed.BulkResponse
import com.lightningkite.rock.template.User
import com.lightningkite.lightningdb.Query
import com.lightningkite.lightningdb.QueryPartial
import com.lightningkite.lightningdb.Partial
import com.lightningkite.UUID
import com.lightningkite.lightningdb.MassModification
import kotlin.Int
import com.lightningkite.lightningdb.Modification
import com.lightningkite.lightningdb.EntryChange
import com.lightningkite.lightningdb.Condition
import kotlin.Unit
import com.lightningkite.lightningdb.GroupCountQuery
import com.lightningkite.lightningdb.AggregateQuery
import kotlin.Double
import com.lightningkite.lightningdb.GroupAggregateQuery
import com.lightningkite.lightningdb.ListChange
import com.lightningkite.lightningserver.auth.proof.FinishProof
import com.lightningkite.lightningserver.auth.proof.Proof
import com.lightningkite.lightningserver.auth.proof.EstablishOtp
import kotlin.Boolean
import com.lightningkite.lightningserver.auth.proof.SecretMetadata
import com.lightningkite.lightningserver.auth.proof.IdentificationAndPassword
import com.lightningkite.lightningserver.auth.proof.EstablishPassword
import com.lightningkite.lightningserver.auth.subject.IdAndAuthMethods
import com.lightningkite.lightningserver.auth.subject.SubSessionRequest
import com.lightningkite.lightningserver.auth.oauth.OauthTokenRequest
import com.lightningkite.lightningserver.auth.oauth.OauthResponse
import com.lightningkite.lightningserver.auth.subject.Session
import com.lightningkite.rock.template.FcmToken
import com.lightningkite.rock.template.PublicMessage
import com.lightningkite.rock.template.Notification

interface Api {
    val user: UserApi
    val smsProof: SmsProofApi
    val emailProof: EmailProofApi
    val oneTimePasswordProof: OneTimePasswordProofApi
    val passwordProof: PasswordProofApi
    val userAuth: UserAuthApi
    val userSession: UserSessionApi
    val fcmToken: FcmTokenApi
    val publicMessage: PublicMessageApi
    val notification: NotificationApi
    suspend fun logInViaApple(): String
    suspend fun logInViaGoogle(): String
    suspend fun logInViaMicrosoft(): String
    suspend fun uploadFileForRequest(): UploadInformation
    suspend fun getServerHealth(userAccessToken: suspend () -> String): ServerHealth
    suspend fun bulkRequest(input: Map<String, BulkRequest>): Map<String, BulkResponse>
    interface UserApi {
        suspend fun default(userAccessToken: suspend () -> String): User
        suspend fun query(input: Query<User>, userAccessToken: suspend () -> String): List<User>
        suspend fun queryPartial(input: QueryPartial<User>, userAccessToken: suspend () -> String): List<Partial<User>>
        suspend fun detail(id: UUID, userAccessToken: suspend () -> String): User
        suspend fun insertBulk(input: List<User>, userAccessToken: suspend () -> String): List<User>
        suspend fun insert(input: User, userAccessToken: suspend () -> String): User
        suspend fun upsert(id: UUID, input: User, userAccessToken: suspend () -> String): User
        suspend fun bulkReplace(input: List<User>, userAccessToken: suspend () -> String): List<User>
        suspend fun replace(id: UUID, input: User, userAccessToken: suspend () -> String): User
        suspend fun bulkModify(input: MassModification<User>, userAccessToken: suspend () -> String): Int
        suspend fun modifyWithDiff(id: UUID, input: Modification<User>, userAccessToken: suspend () -> String): EntryChange<User>
        suspend fun modify(id: UUID, input: Modification<User>, userAccessToken: suspend () -> String): User
        suspend fun bulkDelete(input: Condition<User>, userAccessToken: suspend () -> String): Int
        suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit
        suspend fun count(input: Condition<User>, userAccessToken: suspend () -> String): Int
        suspend fun groupCount(input: GroupCountQuery<User>, userAccessToken: suspend () -> String): Map<String, Int>
        suspend fun aggregate(input: AggregateQuery<User>, userAccessToken: suspend () -> String): Double?
        suspend fun groupAggregate(input: GroupAggregateQuery<User>, userAccessToken: suspend () -> String): Map<String, Double?>
        suspend fun watch(userToken: String): TypedWebSocket<Query<User>, ListChange<User>>
    }
    interface SmsProofApi {
        suspend fun beginSmsOwnershipProof(input: String): String
        suspend fun provePhoneOwnership(input: FinishProof): Proof
    }
    interface EmailProofApi {
        suspend fun beginEmailOwnershipProof(input: String): String
        suspend fun proveEmailOwnership(input: FinishProof): Proof
    }
    interface OneTimePasswordProofApi {
        suspend fun establishAnOneTimePassword(input: EstablishOtp): String
        suspend fun confirmOneTimePassword(input: String): Unit
        suspend fun disableOneTimePassword(): Boolean
        suspend fun checkOneTimePassword(): SecretMetadata?
        suspend fun proveOTP(input: IdentificationAndPassword): Proof
    }
    interface PasswordProofApi {
        suspend fun establishAPassword(input: EstablishPassword): Unit
        suspend fun disablePassword(): Boolean
        suspend fun checkPassword(): SecretMetadata?
        suspend fun provePasswordOwnership(input: IdentificationAndPassword): Proof
    }
    interface UserAuthApi {
        suspend fun logIn(input: List<Proof>): IdAndAuthMethods<UUID>
        suspend fun openSession(input: String): String
        suspend fun createSubSession(input: SubSessionRequest, userAccessToken: suspend () -> String): String
        suspend fun getToken(input: OauthTokenRequest): OauthResponse
        suspend fun getTokenSimple(input: String): String
        suspend fun getSelf(userAccessToken: suspend () -> String): User
    }
    interface UserSessionApi {
        suspend fun query(input: Query<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>>
        suspend fun queryPartial(input: QueryPartial<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Partial<Session<User, UUID>>>
        suspend fun detail(id: UUID, userAccessToken: suspend () -> String): Session<User, UUID>
        suspend fun insertBulk(input: List<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>>
        suspend fun insert(input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID>
        suspend fun upsert(id: UUID, input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID>
        suspend fun bulkReplace(input: List<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>>
        suspend fun replace(id: UUID, input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID>
        suspend fun bulkModify(input: MassModification<Session<User, UUID>>, userAccessToken: suspend () -> String): Int
        suspend fun modifyWithDiff(id: UUID, input: Modification<Session<User, UUID>>, userAccessToken: suspend () -> String): EntryChange<Session<User, UUID>>
        suspend fun modify(id: UUID, input: Modification<Session<User, UUID>>, userAccessToken: suspend () -> String): Session<User, UUID>
        suspend fun bulkDelete(input: Condition<Session<User, UUID>>, userAccessToken: suspend () -> String): Int
        suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit
        suspend fun count(input: Condition<Session<User, UUID>>, userAccessToken: suspend () -> String): Int
        suspend fun groupCount(input: GroupCountQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Map<String, Int>
        suspend fun aggregate(input: AggregateQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Double?
        suspend fun groupAggregate(input: GroupAggregateQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Map<String, Double?>
        suspend fun terminateSession(userAccessToken: suspend () -> String): Unit
        suspend fun terminateOtherSession(sessionId: UUID, userAccessToken: suspend () -> String): Unit
    }
    interface FcmTokenApi {
        suspend fun default(userAccessToken: suspend () -> String): FcmToken
        suspend fun query(input: Query<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken>
        suspend fun queryPartial(input: QueryPartial<FcmToken>, userAccessToken: suspend () -> String): List<Partial<FcmToken>>
        suspend fun detail(id: String, userAccessToken: suspend () -> String): FcmToken
        suspend fun insertBulk(input: List<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken>
        suspend fun insert(input: FcmToken, userAccessToken: suspend () -> String): FcmToken
        suspend fun upsert(id: String, input: FcmToken, userAccessToken: suspend () -> String): FcmToken
        suspend fun bulkReplace(input: List<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken>
        suspend fun replace(id: String, input: FcmToken, userAccessToken: suspend () -> String): FcmToken
        suspend fun bulkModify(input: MassModification<FcmToken>, userAccessToken: suspend () -> String): Int
        suspend fun modifyWithDiff(id: String, input: Modification<FcmToken>, userAccessToken: suspend () -> String): EntryChange<FcmToken>
        suspend fun modify(id: String, input: Modification<FcmToken>, userAccessToken: suspend () -> String): FcmToken
        suspend fun bulkDelete(input: Condition<FcmToken>, userAccessToken: suspend () -> String): Int
        suspend fun delete(id: String, userAccessToken: suspend () -> String): Unit
        suspend fun count(input: Condition<FcmToken>, userAccessToken: suspend () -> String): Int
        suspend fun groupCount(input: GroupCountQuery<FcmToken>, userAccessToken: suspend () -> String): Map<String, Int>
        suspend fun aggregate(input: AggregateQuery<FcmToken>, userAccessToken: suspend () -> String): Double?
        suspend fun groupAggregate(input: GroupAggregateQuery<FcmToken>, userAccessToken: suspend () -> String): Map<String, Double?>
    }
    interface PublicMessageApi {
        suspend fun default(userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun query(input: Query<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage>
        suspend fun queryPartial(input: QueryPartial<PublicMessage>, userAccessToken: (suspend () -> String)?): List<Partial<PublicMessage>>
        suspend fun detail(id: UUID, userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun insertBulk(input: List<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage>
        suspend fun insert(input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun upsert(id: UUID, input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun bulkReplace(input: List<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage>
        suspend fun replace(id: UUID, input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun bulkModify(input: MassModification<PublicMessage>, userAccessToken: (suspend () -> String)?): Int
        suspend fun modifyWithDiff(id: UUID, input: Modification<PublicMessage>, userAccessToken: (suspend () -> String)?): EntryChange<PublicMessage>
        suspend fun modify(id: UUID, input: Modification<PublicMessage>, userAccessToken: (suspend () -> String)?): PublicMessage
        suspend fun bulkDelete(input: Condition<PublicMessage>, userAccessToken: (suspend () -> String)?): Int
        suspend fun delete(id: UUID, userAccessToken: (suspend () -> String)?): Unit
        suspend fun count(input: Condition<PublicMessage>, userAccessToken: (suspend () -> String)?): Int
        suspend fun groupCount(input: GroupCountQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Map<String, Int>
        suspend fun aggregate(input: AggregateQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Double?
        suspend fun groupAggregate(input: GroupAggregateQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Map<String, Double?>
        suspend fun watch(userToken: String?): TypedWebSocket<Query<PublicMessage>, ListChange<PublicMessage>>
    }
    interface NotificationApi {
        suspend fun default(userAccessToken: suspend () -> String): Notification
        suspend fun query(input: Query<Notification>, userAccessToken: suspend () -> String): List<Notification>
        suspend fun queryPartial(input: QueryPartial<Notification>, userAccessToken: suspend () -> String): List<Partial<Notification>>
        suspend fun detail(id: UUID, userAccessToken: suspend () -> String): Notification
        suspend fun insertBulk(input: List<Notification>, userAccessToken: suspend () -> String): List<Notification>
        suspend fun insert(input: Notification, userAccessToken: suspend () -> String): Notification
        suspend fun upsert(id: UUID, input: Notification, userAccessToken: suspend () -> String): Notification
        suspend fun bulkReplace(input: List<Notification>, userAccessToken: suspend () -> String): List<Notification>
        suspend fun replace(id: UUID, input: Notification, userAccessToken: suspend () -> String): Notification
        suspend fun bulkModify(input: MassModification<Notification>, userAccessToken: suspend () -> String): Int
        suspend fun modifyWithDiff(id: UUID, input: Modification<Notification>, userAccessToken: suspend () -> String): EntryChange<Notification>
        suspend fun modify(id: UUID, input: Modification<Notification>, userAccessToken: suspend () -> String): Notification
        suspend fun bulkDelete(input: Condition<Notification>, userAccessToken: suspend () -> String): Int
        suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit
        suspend fun count(input: Condition<Notification>, userAccessToken: suspend () -> String): Int
        suspend fun groupCount(input: GroupCountQuery<Notification>, userAccessToken: suspend () -> String): Map<String, Int>
        suspend fun aggregate(input: AggregateQuery<Notification>, userAccessToken: suspend () -> String): Double?
        suspend fun groupAggregate(input: GroupAggregateQuery<Notification>, userAccessToken: suspend () -> String): Map<String, Double?>
        suspend fun watch(userToken: String): TypedWebSocket<Query<Notification>, ListChange<Notification>>
    }
}

