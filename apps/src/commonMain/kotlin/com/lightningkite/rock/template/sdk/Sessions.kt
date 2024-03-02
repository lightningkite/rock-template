@file:SharedCode
package com.lightningkite.rock.template.sdk

import com.lightningkite.khrysalis.SharedCode
import com.lightningkite.*
import com.lightningkite.rock.*
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.db.*
import kotlinx.datetime.*
import kotlin.String
import com.lightningkite.lightningserver.files.UploadInformation
import com.lightningkite.lightningserver.typed.BulkRequest
import com.lightningkite.lightningserver.typed.BulkResponse
import com.lightningkite.lightningserver.auth.proof.FinishProof
import com.lightningkite.lightningserver.auth.proof.Proof
import com.lightningkite.lightningserver.auth.proof.IdentificationAndPassword
import com.lightningkite.lightningserver.auth.subject.IdAndAuthMethods
import com.lightningkite.UUID
import com.lightningkite.lightningserver.auth.oauth.OauthTokenRequest
import com.lightningkite.lightningserver.auth.oauth.OauthResponse
import com.lightningkite.rock.template.PublicMessage
import com.lightningkite.lightningdb.Query
import com.lightningkite.lightningdb.QueryPartial
import com.lightningkite.lightningdb.Partial
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
import com.lightningkite.lightningserver.serverhealth.ServerHealth
import com.lightningkite.rock.template.User
import com.lightningkite.lightningserver.auth.proof.EstablishOtp
import kotlin.Boolean
import com.lightningkite.lightningserver.auth.proof.SecretMetadata
import com.lightningkite.lightningserver.auth.proof.EstablishPassword
import com.lightningkite.lightningserver.auth.subject.SubSessionRequest
import com.lightningkite.lightningserver.auth.subject.Session
import com.lightningkite.rock.template.FcmToken
import com.lightningkite.rock.template.Notification

open class AbstractAnonymousSession(val api: Api) {
    val user: AbstractAnonymousSessionUserApi = AbstractAnonymousSessionUserApi(api.user)
    val smsProof: AbstractAnonymousSessionSmsProofApi = AbstractAnonymousSessionSmsProofApi(api.smsProof)
    val emailProof: AbstractAnonymousSessionEmailProofApi = AbstractAnonymousSessionEmailProofApi(api.emailProof)
    val oneTimePasswordProof: AbstractAnonymousSessionOneTimePasswordProofApi = AbstractAnonymousSessionOneTimePasswordProofApi(api.oneTimePasswordProof)
    val passwordProof: AbstractAnonymousSessionPasswordProofApi = AbstractAnonymousSessionPasswordProofApi(api.passwordProof)
    val userAuth: AbstractAnonymousSessionUserAuthApi = AbstractAnonymousSessionUserAuthApi(api.userAuth)
    val userSession: AbstractAnonymousSessionUserSessionApi = AbstractAnonymousSessionUserSessionApi(api.userSession)
    val fcmToken: AbstractAnonymousSessionFcmTokenApi = AbstractAnonymousSessionFcmTokenApi(api.fcmToken)
    val publicMessage: AbstractAnonymousSessionPublicMessageApi = AbstractAnonymousSessionPublicMessageApi(api.publicMessage)
    val notification: AbstractAnonymousSessionNotificationApi = AbstractAnonymousSessionNotificationApi(api.notification)
    suspend fun logInViaApple(): String = api.logInViaApple()
    suspend fun logInViaGoogle(): String = api.logInViaGoogle()
    suspend fun logInViaMicrosoft(): String = api.logInViaMicrosoft()
    suspend fun uploadFileForRequest(): UploadInformation = api.uploadFileForRequest()
    suspend fun bulkRequest(input: Map<String, BulkRequest>): Map<String, BulkResponse> = api.bulkRequest(input)
    open class AbstractAnonymousSessionUserApi(val api: Api.UserApi) {
    }
    open class AbstractAnonymousSessionSmsProofApi(val api: Api.SmsProofApi) {
        suspend fun beginSmsOwnershipProof(input: String): String = api.beginSmsOwnershipProof(input)
        suspend fun provePhoneOwnership(input: FinishProof): Proof = api.provePhoneOwnership(input)
    }
    open class AbstractAnonymousSessionEmailProofApi(val api: Api.EmailProofApi) {
        suspend fun beginEmailOwnershipProof(input: String): String = api.beginEmailOwnershipProof(input)
        suspend fun proveEmailOwnership(input: FinishProof): Proof = api.proveEmailOwnership(input)
    }
    open class AbstractAnonymousSessionOneTimePasswordProofApi(val api: Api.OneTimePasswordProofApi) {
        suspend fun proveOTP(input: IdentificationAndPassword): Proof = api.proveOTP(input)
    }
    open class AbstractAnonymousSessionPasswordProofApi(val api: Api.PasswordProofApi) {
        suspend fun provePasswordOwnership(input: IdentificationAndPassword): Proof = api.provePasswordOwnership(input)
    }
    open class AbstractAnonymousSessionUserAuthApi(val api: Api.UserAuthApi) {
        suspend fun logIn(input: List<Proof>): IdAndAuthMethods<UUID> = api.logIn(input)
        suspend fun openSession(input: String): String = api.openSession(input)
        suspend fun getToken(input: OauthTokenRequest): OauthResponse = api.getToken(input)
        suspend fun getTokenSimple(input: String): String = api.getTokenSimple(input)
    }
    open class AbstractAnonymousSessionUserSessionApi(val api: Api.UserSessionApi) {
    }
    open class AbstractAnonymousSessionFcmTokenApi(val api: Api.FcmTokenApi) {
    }
    open class AbstractAnonymousSessionPublicMessageApi(val api: Api.PublicMessageApi): ModelRestEndpoints<PublicMessage, UUID>, ModelRestEndpointsPlusWs<PublicMessage, UUID> {
        override suspend fun default(): PublicMessage = api.default(null)
        override suspend fun query(input: Query<PublicMessage>): List<PublicMessage> = api.query(input, null)
        override suspend fun queryPartial(input: QueryPartial<PublicMessage>): List<Partial<PublicMessage>> = api.queryPartial(input, null)
        override suspend fun detail(id: UUID): PublicMessage = api.detail(id, null)
        override suspend fun insertBulk(input: List<PublicMessage>): List<PublicMessage> = api.insertBulk(input, null)
        override suspend fun insert(input: PublicMessage): PublicMessage = api.insert(input, null)
        override suspend fun upsert(id: UUID, input: PublicMessage): PublicMessage = api.upsert(id, input, null)
        override suspend fun bulkReplace(input: List<PublicMessage>): List<PublicMessage> = api.bulkReplace(input, null)
        override suspend fun replace(id: UUID, input: PublicMessage): PublicMessage = api.replace(id, input, null)
        override suspend fun bulkModify(input: MassModification<PublicMessage>): Int = api.bulkModify(input, null)
        override suspend fun modifyWithDiff(id: UUID, input: Modification<PublicMessage>): EntryChange<PublicMessage> = api.modifyWithDiff(id, input, null)
        override suspend fun modify(id: UUID, input: Modification<PublicMessage>): PublicMessage = api.modify(id, input, null)
        override suspend fun bulkDelete(input: Condition<PublicMessage>): Int = api.bulkDelete(input, null)
        override suspend fun delete(id: UUID): Unit = api.delete(id, null)
        override suspend fun count(input: Condition<PublicMessage>): Int = api.count(input, null)
        override suspend fun groupCount(input: GroupCountQuery<PublicMessage>): Map<String, Int> = api.groupCount(input, null)
        override suspend fun aggregate(input: AggregateQuery<PublicMessage>): Double? = api.aggregate(input, null)
        override suspend fun groupAggregate(input: GroupAggregateQuery<PublicMessage>): Map<String, Double?> = api.groupAggregate(input, null)
        override suspend fun watch(): TypedWebSocket<Query<PublicMessage>, ListChange<PublicMessage>> = api.watch(null)
    }
    open class AbstractAnonymousSessionNotificationApi(val api: Api.NotificationApi) {
    }
}

abstract class AbstractUserSession(api: Api, userToken: String, userAccessToken: suspend () -> String) {
    abstract val api: Api
    abstract val userToken: String
    abstract val userAccessToken: suspend () -> String
    val user: UserSessionUserApi = UserSessionUserApi(api.user, userToken, userAccessToken)
    val smsProof: UserSessionSmsProofApi = UserSessionSmsProofApi(api.smsProof, userToken, userAccessToken)
    val emailProof: UserSessionEmailProofApi = UserSessionEmailProofApi(api.emailProof, userToken, userAccessToken)
    val oneTimePasswordProof: UserSessionOneTimePasswordProofApi = UserSessionOneTimePasswordProofApi(api.oneTimePasswordProof, userToken, userAccessToken)
    val passwordProof: UserSessionPasswordProofApi = UserSessionPasswordProofApi(api.passwordProof, userToken, userAccessToken)
    val userAuth: UserSessionUserAuthApi = UserSessionUserAuthApi(api.userAuth, userToken, userAccessToken)
    val userSession: UserSessionUserSessionApi = UserSessionUserSessionApi(api.userSession, userToken, userAccessToken)
    val fcmToken: UserSessionFcmTokenApi = UserSessionFcmTokenApi(api.fcmToken, userToken, userAccessToken)
    val publicMessage: UserSessionPublicMessageApi = UserSessionPublicMessageApi(api.publicMessage, userToken, userAccessToken)
    val notification: UserSessionNotificationApi = UserSessionNotificationApi(api.notification, userToken, userAccessToken)
    suspend fun logInViaApple(): String = api.logInViaApple()
    suspend fun logInViaGoogle(): String = api.logInViaGoogle()
    suspend fun logInViaMicrosoft(): String = api.logInViaMicrosoft()
    suspend fun uploadFileForRequest(): UploadInformation = api.uploadFileForRequest()
    suspend fun getServerHealth(): ServerHealth = api.getServerHealth(userAccessToken)
    suspend fun bulkRequest(input: Map<String, BulkRequest>): Map<String, BulkResponse> = api.bulkRequest(input)
    class UserSessionUserApi(val api: Api.UserApi,val userToken:String, val userAccessToken: suspend () -> String): ModelRestEndpoints<User, UUID>, ModelRestEndpointsPlusWs<User, UUID> {
        override suspend fun default(): User = api.default(userAccessToken)
        override suspend fun query(input: Query<User>): List<User> = api.query(input, userAccessToken)
        override suspend fun queryPartial(input: QueryPartial<User>): List<Partial<User>> = api.queryPartial(input, userAccessToken)
        override suspend fun detail(id: UUID): User = api.detail(id, userAccessToken)
        override suspend fun insertBulk(input: List<User>): List<User> = api.insertBulk(input, userAccessToken)
        override suspend fun insert(input: User): User = api.insert(input, userAccessToken)
        override suspend fun upsert(id: UUID, input: User): User = api.upsert(id, input, userAccessToken)
        override suspend fun bulkReplace(input: List<User>): List<User> = api.bulkReplace(input, userAccessToken)
        override suspend fun replace(id: UUID, input: User): User = api.replace(id, input, userAccessToken)
        override suspend fun bulkModify(input: MassModification<User>): Int = api.bulkModify(input, userAccessToken)
        override suspend fun modifyWithDiff(id: UUID, input: Modification<User>): EntryChange<User> = api.modifyWithDiff(id, input, userAccessToken)
        override suspend fun modify(id: UUID, input: Modification<User>): User = api.modify(id, input, userAccessToken)
        override suspend fun bulkDelete(input: Condition<User>): Int = api.bulkDelete(input, userAccessToken)
        override suspend fun delete(id: UUID): Unit = api.delete(id, userAccessToken)
        override suspend fun count(input: Condition<User>): Int = api.count(input, userAccessToken)
        override suspend fun groupCount(input: GroupCountQuery<User>): Map<String, Int> = api.groupCount(input, userAccessToken)
        override suspend fun aggregate(input: AggregateQuery<User>): Double? = api.aggregate(input, userAccessToken)
        override suspend fun groupAggregate(input: GroupAggregateQuery<User>): Map<String, Double?> = api.groupAggregate(input, userAccessToken)
        override suspend fun watch(): TypedWebSocket<Query<User>, ListChange<User>> = api.watch(userToken)
    }
    class UserSessionSmsProofApi(val api: Api.SmsProofApi,val userToken:String, val userAccessToken: suspend () -> String) {
        suspend fun beginSmsOwnershipProof(input: String): String = api.beginSmsOwnershipProof(input)
        suspend fun provePhoneOwnership(input: FinishProof): Proof = api.provePhoneOwnership(input)
    }
    class UserSessionEmailProofApi(val api: Api.EmailProofApi,val userToken:String, val userAccessToken: suspend () -> String) {
        suspend fun beginEmailOwnershipProof(input: String): String = api.beginEmailOwnershipProof(input)
        suspend fun proveEmailOwnership(input: FinishProof): Proof = api.proveEmailOwnership(input)
    }
    class UserSessionOneTimePasswordProofApi(val api: Api.OneTimePasswordProofApi,val userToken:String, val userAccessToken: suspend () -> String) {
        suspend fun establishAnOneTimePassword(input: EstablishOtp): String = api.establishAnOneTimePassword(input)
        suspend fun confirmOneTimePassword(input: String): Unit = api.confirmOneTimePassword(input)
        suspend fun disableOneTimePassword(): Boolean = api.disableOneTimePassword()
        suspend fun checkOneTimePassword(): SecretMetadata? = api.checkOneTimePassword()
        suspend fun proveOTP(input: IdentificationAndPassword): Proof = api.proveOTP(input)
    }
    class UserSessionPasswordProofApi(val api: Api.PasswordProofApi,val userToken:String, val userAccessToken: suspend () -> String) {
        suspend fun establishAPassword(input: EstablishPassword): Unit = api.establishAPassword(input)
        suspend fun disablePassword(): Boolean = api.disablePassword()
        suspend fun checkPassword(): SecretMetadata? = api.checkPassword()
        suspend fun provePasswordOwnership(input: IdentificationAndPassword): Proof = api.provePasswordOwnership(input)
    }
    class UserSessionUserAuthApi(val api: Api.UserAuthApi,val userToken:String, val userAccessToken: suspend () -> String) {
        suspend fun logIn(input: List<Proof>): IdAndAuthMethods<UUID> = api.logIn(input)
        suspend fun openSession(input: String): String = api.openSession(input)
        suspend fun createSubSession(input: SubSessionRequest): String = api.createSubSession(input, userAccessToken)
        suspend fun getToken(input: OauthTokenRequest): OauthResponse = api.getToken(input)
        suspend fun getTokenSimple(input: String): String = api.getTokenSimple(input)
        suspend fun getSelf(): User = api.getSelf(userAccessToken)
    }
    class UserSessionUserSessionApi(val api: Api.UserSessionApi,val userToken:String, val userAccessToken: suspend () -> String): ModelRestEndpoints<Session<User, UUID>, UUID> {
        override suspend fun query(input: Query<Session<User, UUID>>): List<Session<User, UUID>> = api.query(input, userAccessToken)
        override suspend fun queryPartial(input: QueryPartial<Session<User, UUID>>): List<Partial<Session<User, UUID>>> = api.queryPartial(input, userAccessToken)
        override suspend fun detail(id: UUID): Session<User, UUID> = api.detail(id, userAccessToken)
        override suspend fun insertBulk(input: List<Session<User, UUID>>): List<Session<User, UUID>> = api.insertBulk(input, userAccessToken)
        override suspend fun insert(input: Session<User, UUID>): Session<User, UUID> = api.insert(input, userAccessToken)
        override suspend fun upsert(id: UUID, input: Session<User, UUID>): Session<User, UUID> = api.upsert(id, input, userAccessToken)
        override suspend fun bulkReplace(input: List<Session<User, UUID>>): List<Session<User, UUID>> = api.bulkReplace(input, userAccessToken)
        override suspend fun replace(id: UUID, input: Session<User, UUID>): Session<User, UUID> = api.replace(id, input, userAccessToken)
        override suspend fun bulkModify(input: MassModification<Session<User, UUID>>): Int = api.bulkModify(input, userAccessToken)
        override suspend fun modifyWithDiff(id: UUID, input: Modification<Session<User, UUID>>): EntryChange<Session<User, UUID>> = api.modifyWithDiff(id, input, userAccessToken)
        override suspend fun modify(id: UUID, input: Modification<Session<User, UUID>>): Session<User, UUID> = api.modify(id, input, userAccessToken)
        override suspend fun bulkDelete(input: Condition<Session<User, UUID>>): Int = api.bulkDelete(input, userAccessToken)
        override suspend fun delete(id: UUID): Unit = api.delete(id, userAccessToken)
        override suspend fun count(input: Condition<Session<User, UUID>>): Int = api.count(input, userAccessToken)
        override suspend fun groupCount(input: GroupCountQuery<Session<User, UUID>>): Map<String, Int> = api.groupCount(input, userAccessToken)
        override suspend fun aggregate(input: AggregateQuery<Session<User, UUID>>): Double? = api.aggregate(input, userAccessToken)
        override suspend fun groupAggregate(input: GroupAggregateQuery<Session<User, UUID>>): Map<String, Double?> = api.groupAggregate(input, userAccessToken)
        suspend fun terminateSession(): Unit = api.terminateSession(userAccessToken)
        suspend fun terminateOtherSession(sessionId: UUID): Unit = api.terminateOtherSession(sessionId, userAccessToken)
    }
    class UserSessionFcmTokenApi(val api: Api.FcmTokenApi,val userToken:String, val userAccessToken: suspend () -> String): ModelRestEndpoints<FcmToken, String> {
        override suspend fun default(): FcmToken = api.default(userAccessToken)
        override suspend fun query(input: Query<FcmToken>): List<FcmToken> = api.query(input, userAccessToken)
        override suspend fun queryPartial(input: QueryPartial<FcmToken>): List<Partial<FcmToken>> = api.queryPartial(input, userAccessToken)
        override suspend fun detail(id: String): FcmToken = api.detail(id, userAccessToken)
        override suspend fun insertBulk(input: List<FcmToken>): List<FcmToken> = api.insertBulk(input, userAccessToken)
        override suspend fun insert(input: FcmToken): FcmToken = api.insert(input, userAccessToken)
        override suspend fun upsert(id: String, input: FcmToken): FcmToken = api.upsert(id, input, userAccessToken)
        override suspend fun bulkReplace(input: List<FcmToken>): List<FcmToken> = api.bulkReplace(input, userAccessToken)
        override suspend fun replace(id: String, input: FcmToken): FcmToken = api.replace(id, input, userAccessToken)
        override suspend fun bulkModify(input: MassModification<FcmToken>): Int = api.bulkModify(input, userAccessToken)
        override suspend fun modifyWithDiff(id: String, input: Modification<FcmToken>): EntryChange<FcmToken> = api.modifyWithDiff(id, input, userAccessToken)
        override suspend fun modify(id: String, input: Modification<FcmToken>): FcmToken = api.modify(id, input, userAccessToken)
        override suspend fun bulkDelete(input: Condition<FcmToken>): Int = api.bulkDelete(input, userAccessToken)
        override suspend fun delete(id: String): Unit = api.delete(id, userAccessToken)
        override suspend fun count(input: Condition<FcmToken>): Int = api.count(input, userAccessToken)
        override suspend fun groupCount(input: GroupCountQuery<FcmToken>): Map<String, Int> = api.groupCount(input, userAccessToken)
        override suspend fun aggregate(input: AggregateQuery<FcmToken>): Double? = api.aggregate(input, userAccessToken)
        override suspend fun groupAggregate(input: GroupAggregateQuery<FcmToken>): Map<String, Double?> = api.groupAggregate(input, userAccessToken)
    }
    class UserSessionPublicMessageApi(val api: Api.PublicMessageApi,val userToken:String, val userAccessToken: suspend () -> String): ModelRestEndpoints<PublicMessage, UUID>, ModelRestEndpointsPlusWs<PublicMessage, UUID> {
        override suspend fun default(): PublicMessage = api.default(userAccessToken)
        override suspend fun query(input: Query<PublicMessage>): List<PublicMessage> = api.query(input, userAccessToken)
        override suspend fun queryPartial(input: QueryPartial<PublicMessage>): List<Partial<PublicMessage>> = api.queryPartial(input, userAccessToken)
        override suspend fun detail(id: UUID): PublicMessage = api.detail(id, userAccessToken)
        override suspend fun insertBulk(input: List<PublicMessage>): List<PublicMessage> = api.insertBulk(input, userAccessToken)
        override suspend fun insert(input: PublicMessage): PublicMessage = api.insert(input, userAccessToken)
        override suspend fun upsert(id: UUID, input: PublicMessage): PublicMessage = api.upsert(id, input, userAccessToken)
        override suspend fun bulkReplace(input: List<PublicMessage>): List<PublicMessage> = api.bulkReplace(input, userAccessToken)
        override suspend fun replace(id: UUID, input: PublicMessage): PublicMessage = api.replace(id, input, userAccessToken)
        override suspend fun bulkModify(input: MassModification<PublicMessage>): Int = api.bulkModify(input, userAccessToken)
        override suspend fun modifyWithDiff(id: UUID, input: Modification<PublicMessage>): EntryChange<PublicMessage> = api.modifyWithDiff(id, input, userAccessToken)
        override suspend fun modify(id: UUID, input: Modification<PublicMessage>): PublicMessage = api.modify(id, input, userAccessToken)
        override suspend fun bulkDelete(input: Condition<PublicMessage>): Int = api.bulkDelete(input, userAccessToken)
        override suspend fun delete(id: UUID): Unit = api.delete(id, userAccessToken)
        override suspend fun count(input: Condition<PublicMessage>): Int = api.count(input, userAccessToken)
        override suspend fun groupCount(input: GroupCountQuery<PublicMessage>): Map<String, Int> = api.groupCount(input, userAccessToken)
        override suspend fun aggregate(input: AggregateQuery<PublicMessage>): Double? = api.aggregate(input, userAccessToken)
        override suspend fun groupAggregate(input: GroupAggregateQuery<PublicMessage>): Map<String, Double?> = api.groupAggregate(input, userAccessToken)
        override suspend fun watch(): TypedWebSocket<Query<PublicMessage>, ListChange<PublicMessage>> = api.watch(userToken)
    }
    class UserSessionNotificationApi(val api: Api.NotificationApi,val userToken:String, val userAccessToken: suspend () -> String): ModelRestEndpoints<Notification, UUID>, ModelRestEndpointsPlusWs<Notification, UUID> {
        override suspend fun default(): Notification = api.default(userAccessToken)
        override suspend fun query(input: Query<Notification>): List<Notification> = api.query(input, userAccessToken)
        override suspend fun queryPartial(input: QueryPartial<Notification>): List<Partial<Notification>> = api.queryPartial(input, userAccessToken)
        override suspend fun detail(id: UUID): Notification = api.detail(id, userAccessToken)
        override suspend fun insertBulk(input: List<Notification>): List<Notification> = api.insertBulk(input, userAccessToken)
        override suspend fun insert(input: Notification): Notification = api.insert(input, userAccessToken)
        override suspend fun upsert(id: UUID, input: Notification): Notification = api.upsert(id, input, userAccessToken)
        override suspend fun bulkReplace(input: List<Notification>): List<Notification> = api.bulkReplace(input, userAccessToken)
        override suspend fun replace(id: UUID, input: Notification): Notification = api.replace(id, input, userAccessToken)
        override suspend fun bulkModify(input: MassModification<Notification>): Int = api.bulkModify(input, userAccessToken)
        override suspend fun modifyWithDiff(id: UUID, input: Modification<Notification>): EntryChange<Notification> = api.modifyWithDiff(id, input, userAccessToken)
        override suspend fun modify(id: UUID, input: Modification<Notification>): Notification = api.modify(id, input, userAccessToken)
        override suspend fun bulkDelete(input: Condition<Notification>): Int = api.bulkDelete(input, userAccessToken)
        override suspend fun delete(id: UUID): Unit = api.delete(id, userAccessToken)
        override suspend fun count(input: Condition<Notification>): Int = api.count(input, userAccessToken)
        override suspend fun groupCount(input: GroupCountQuery<Notification>): Map<String, Int> = api.groupCount(input, userAccessToken)
        override suspend fun aggregate(input: AggregateQuery<Notification>): Double? = api.aggregate(input, userAccessToken)
        override suspend fun groupAggregate(input: GroupAggregateQuery<Notification>): Map<String, Double?> = api.groupAggregate(input, userAccessToken)
        override suspend fun watch(): TypedWebSocket<Query<Notification>, ListChange<Notification>> = api.watch(userToken)
    }
}

