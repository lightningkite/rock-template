@file:SharedCode
package com.lightningkite.rock.template.sdk

import com.lightningkite.khrysalis.SharedCode
import com.lightningkite.*
import com.lightningkite.lightningdb.*
import com.lightningkite.rock.*
import kotlinx.datetime.*
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

class LiveApi(val httpUrl: String, val socketUrl: String): Api {
    override val user: Api.UserApi = LiveUserApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val smsProof: Api.SmsProofApi = LiveSmsProofApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val emailProof: Api.EmailProofApi = LiveEmailProofApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val oneTimePasswordProof: Api.OneTimePasswordProofApi = LiveOneTimePasswordProofApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val passwordProof: Api.PasswordProofApi = LivePasswordProofApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val userAuth: Api.UserAuthApi = LiveUserAuthApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val userSession: Api.UserSessionApi = LiveUserSessionApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val fcmToken: Api.FcmTokenApi = LiveFcmTokenApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val publicMessage: Api.PublicMessageApi = LivePublicMessageApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override val notification: Api.NotificationApi = LiveNotificationApi(httpUrl = httpUrl, socketUrl = socketUrl)
    override suspend fun logInViaApple(): String = fetch(
        url = "$httpUrl/proof/oauth/apple/login",
        method = HttpMethod.GET,
    )
    override suspend fun logInViaGoogle(): String = fetch(
        url = "$httpUrl/proof/oauth/google/login",
        method = HttpMethod.GET,
    )
    override suspend fun logInViaMicrosoft(): String = fetch(
        url = "$httpUrl/proof/oauth/microsoft/login",
        method = HttpMethod.GET,
    )
    override suspend fun uploadFileForRequest(): UploadInformation = fetch(
        url = "$httpUrl/upload-early",
        method = HttpMethod.GET,
    )
    override suspend fun getServerHealth(userAccessToken: suspend () -> String): ServerHealth = fetch(
        url = "$httpUrl/meta/health",
        method = HttpMethod.GET,
            token = userAccessToken,
    )
    override suspend fun bulkRequest(input: Map<String, BulkRequest>): Map<String, BulkResponse> = fetch(
        url = "$httpUrl/meta/bulk",
        method = HttpMethod.POST,
        body = input
    )
    class LiveUserApi(val httpUrl: String, val socketUrl: String): Api.UserApi {
        override suspend fun default(userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest/_default_",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun query(input: Query<User>, userAccessToken: suspend () -> String): List<User> = fetch(
            url = "$httpUrl/users/rest/query",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun queryPartial(input: QueryPartial<User>, userAccessToken: suspend () -> String): List<Partial<User>> = fetch(
            url = "$httpUrl/users/rest/query-partial",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun detail(id: UUID, userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun insertBulk(input: List<User>, userAccessToken: suspend () -> String): List<User> = fetch(
            url = "$httpUrl/users/rest/bulk",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun insert(input: User, userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun upsert(id: UUID, input: User, userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkReplace(input: List<User>, userAccessToken: suspend () -> String): List<User> = fetch(
            url = "$httpUrl/users/rest",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun replace(id: UUID, input: User, userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkModify(input: MassModification<User>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/users/rest/bulk",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modifyWithDiff(id: UUID, input: Modification<User>, userAccessToken: suspend () -> String): EntryChange<User> = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}/delta",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modify(id: UUID, input: Modification<User>, userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkDelete(input: Condition<User>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/users/rest/bulk-delete",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/users/rest/${id.urlify()}",
            method = HttpMethod.DELETE,
            token = userAccessToken,
        )
        override suspend fun count(input: Condition<User>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/users/rest/count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupCount(input: GroupCountQuery<User>, userAccessToken: suspend () -> String): Map<String, Int> = fetch(
            url = "$httpUrl/users/rest/group-count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun aggregate(input: AggregateQuery<User>, userAccessToken: suspend () -> String): Double? = fetch(
            url = "$httpUrl/users/rest/aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupAggregate(input: GroupAggregateQuery<User>, userAccessToken: suspend () -> String): Map<String, Double?> = fetch(
            url = "$httpUrl/users/rest/group-aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun watch(userToken: String): TypedWebSocket<Query<User>, ListChange<User>> = multiplexedSocket(
            socketUrl = socketUrl, 
            path = "/users/rest", 
            token = userToken,
        )
    }
    class LiveSmsProofApi(val httpUrl: String, val socketUrl: String): Api.SmsProofApi {
        override suspend fun beginSmsOwnershipProof(input: String): String = fetch(
            url = "$httpUrl/proof/phone/start",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun provePhoneOwnership(input: FinishProof): Proof = fetch(
            url = "$httpUrl/proof/phone/prove",
            method = HttpMethod.POST,
            body = input
        )
    }
    class LiveEmailProofApi(val httpUrl: String, val socketUrl: String): Api.EmailProofApi {
        override suspend fun beginEmailOwnershipProof(input: String): String = fetch(
            url = "$httpUrl/proof/email/start",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun proveEmailOwnership(input: FinishProof): Proof = fetch(
            url = "$httpUrl/proof/email/prove",
            method = HttpMethod.POST,
            body = input
        )
    }
    class LiveOneTimePasswordProofApi(val httpUrl: String, val socketUrl: String): Api.OneTimePasswordProofApi {
        override suspend fun establishAnOneTimePassword(input: EstablishOtp): String = fetch(
            url = "$httpUrl/proof/otp/establish",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun confirmOneTimePassword(input: String): Unit = fetch(
            url = "$httpUrl/proof/otp/existing",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun disableOneTimePassword(): Boolean = fetch(
            url = "$httpUrl/proof/otp/existing",
            method = HttpMethod.DELETE,
        )
        override suspend fun checkOneTimePassword(): SecretMetadata? = fetch(
            url = "$httpUrl/proof/otp/existing",
            method = HttpMethod.GET,
        )
        override suspend fun proveOTP(input: IdentificationAndPassword): Proof = fetch(
            url = "$httpUrl/proof/otp/prove",
            method = HttpMethod.POST,
            body = input
        )
    }
    class LivePasswordProofApi(val httpUrl: String, val socketUrl: String): Api.PasswordProofApi {
        override suspend fun establishAPassword(input: EstablishPassword): Unit = fetch(
            url = "$httpUrl/proof/password/establish",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun disablePassword(): Boolean = fetch(
            url = "$httpUrl/proof/password/existing",
            method = HttpMethod.DELETE,
        )
        override suspend fun checkPassword(): SecretMetadata? = fetch(
            url = "$httpUrl/proof/password/existing",
            method = HttpMethod.GET,
        )
        override suspend fun provePasswordOwnership(input: IdentificationAndPassword): Proof = fetch(
            url = "$httpUrl/proof/password/prove",
            method = HttpMethod.POST,
            body = input
        )
    }
    class LiveUserAuthApi(val httpUrl: String, val socketUrl: String): Api.UserAuthApi {
        override suspend fun logIn(input: List<Proof>): IdAndAuthMethods<UUID> = fetch(
            url = "$httpUrl/auth/login",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun openSession(input: String): String = fetch(
            url = "$httpUrl/auth/open-session",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun createSubSession(input: SubSessionRequest, userAccessToken: suspend () -> String): String = fetch(
            url = "$httpUrl/auth/sub-session",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun getToken(input: OauthTokenRequest): OauthResponse = fetch(
            url = "$httpUrl/auth/token",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun getTokenSimple(input: String): String = fetch(
            url = "$httpUrl/auth/token/simple",
            method = HttpMethod.POST,
            body = input
        )
        override suspend fun getSelf(userAccessToken: suspend () -> String): User = fetch(
            url = "$httpUrl/auth/self",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
    }
    class LiveUserSessionApi(val httpUrl: String, val socketUrl: String): Api.UserSessionApi {
        override suspend fun query(input: Query<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>> = fetch(
            url = "$httpUrl/auth/sessions/query",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun queryPartial(input: QueryPartial<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Partial<Session<User, UUID>>> = fetch(
            url = "$httpUrl/auth/sessions/query-partial",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun detail(id: UUID, userAccessToken: suspend () -> String): Session<User, UUID> = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun insertBulk(input: List<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>> = fetch(
            url = "$httpUrl/auth/sessions/bulk",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun insert(input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID> = fetch(
            url = "$httpUrl/auth/sessions",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun upsert(id: UUID, input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID> = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkReplace(input: List<Session<User, UUID>>, userAccessToken: suspend () -> String): List<Session<User, UUID>> = fetch(
            url = "$httpUrl/auth/sessions",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun replace(id: UUID, input: Session<User, UUID>, userAccessToken: suspend () -> String): Session<User, UUID> = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkModify(input: MassModification<Session<User, UUID>>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/auth/sessions/bulk",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modifyWithDiff(id: UUID, input: Modification<Session<User, UUID>>, userAccessToken: suspend () -> String): EntryChange<Session<User, UUID>> = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}/delta",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modify(id: UUID, input: Modification<Session<User, UUID>>, userAccessToken: suspend () -> String): Session<User, UUID> = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkDelete(input: Condition<Session<User, UUID>>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/auth/sessions/bulk-delete",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/auth/sessions/${id.urlify()}",
            method = HttpMethod.DELETE,
            token = userAccessToken,
        )
        override suspend fun count(input: Condition<Session<User, UUID>>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/auth/sessions/count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupCount(input: GroupCountQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Map<String, Int> = fetch(
            url = "$httpUrl/auth/sessions/group-count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun aggregate(input: AggregateQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Double? = fetch(
            url = "$httpUrl/auth/sessions/aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupAggregate(input: GroupAggregateQuery<Session<User, UUID>>, userAccessToken: suspend () -> String): Map<String, Double?> = fetch(
            url = "$httpUrl/auth/sessions/group-aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun terminateSession(userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/auth/sessions/terminate",
            method = HttpMethod.POST,
            token = userAccessToken,
        )
        override suspend fun terminateOtherSession(sessionId: UUID, userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/auth/sessions/${sessionId.urlify()}/terminate",
            method = HttpMethod.POST,
            token = userAccessToken,
        )
    }
    class LiveFcmTokenApi(val httpUrl: String, val socketUrl: String): Api.FcmTokenApi {
        override suspend fun default(userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens/_default_",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun query(input: Query<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken> = fetch(
            url = "$httpUrl/fcmTokens/query",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun queryPartial(input: QueryPartial<FcmToken>, userAccessToken: suspend () -> String): List<Partial<FcmToken>> = fetch(
            url = "$httpUrl/fcmTokens/query-partial",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun detail(id: String, userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun insertBulk(input: List<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken> = fetch(
            url = "$httpUrl/fcmTokens/bulk",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun insert(input: FcmToken, userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun upsert(id: String, input: FcmToken, userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkReplace(input: List<FcmToken>, userAccessToken: suspend () -> String): List<FcmToken> = fetch(
            url = "$httpUrl/fcmTokens",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun replace(id: String, input: FcmToken, userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkModify(input: MassModification<FcmToken>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/fcmTokens/bulk",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modifyWithDiff(id: String, input: Modification<FcmToken>, userAccessToken: suspend () -> String): EntryChange<FcmToken> = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}/delta",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modify(id: String, input: Modification<FcmToken>, userAccessToken: suspend () -> String): FcmToken = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkDelete(input: Condition<FcmToken>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/fcmTokens/bulk-delete",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun delete(id: String, userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/fcmTokens/${id.urlify()}",
            method = HttpMethod.DELETE,
            token = userAccessToken,
        )
        override suspend fun count(input: Condition<FcmToken>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/fcmTokens/count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupCount(input: GroupCountQuery<FcmToken>, userAccessToken: suspend () -> String): Map<String, Int> = fetch(
            url = "$httpUrl/fcmTokens/group-count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun aggregate(input: AggregateQuery<FcmToken>, userAccessToken: suspend () -> String): Double? = fetch(
            url = "$httpUrl/fcmTokens/aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupAggregate(input: GroupAggregateQuery<FcmToken>, userAccessToken: suspend () -> String): Map<String, Double?> = fetch(
            url = "$httpUrl/fcmTokens/group-aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
    }
    class LivePublicMessageApi(val httpUrl: String, val socketUrl: String): Api.PublicMessageApi {
        override suspend fun default(userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message/_default_",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun query(input: Query<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage> = fetch(
            url = "$httpUrl/public-message/query",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun queryPartial(input: QueryPartial<PublicMessage>, userAccessToken: (suspend () -> String)?): List<Partial<PublicMessage>> = fetch(
            url = "$httpUrl/public-message/query-partial",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun detail(id: UUID, userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message/${id.urlify()}",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun insertBulk(input: List<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage> = fetch(
            url = "$httpUrl/public-message/bulk",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun insert(input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun upsert(id: UUID, input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message/${id.urlify()}",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkReplace(input: List<PublicMessage>, userAccessToken: (suspend () -> String)?): List<PublicMessage> = fetch(
            url = "$httpUrl/public-message",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun replace(id: UUID, input: PublicMessage, userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message/${id.urlify()}",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkModify(input: MassModification<PublicMessage>, userAccessToken: (suspend () -> String)?): Int = fetch(
            url = "$httpUrl/public-message/bulk",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modifyWithDiff(id: UUID, input: Modification<PublicMessage>, userAccessToken: (suspend () -> String)?): EntryChange<PublicMessage> = fetch(
            url = "$httpUrl/public-message/${id.urlify()}/delta",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modify(id: UUID, input: Modification<PublicMessage>, userAccessToken: (suspend () -> String)?): PublicMessage = fetch(
            url = "$httpUrl/public-message/${id.urlify()}",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkDelete(input: Condition<PublicMessage>, userAccessToken: (suspend () -> String)?): Int = fetch(
            url = "$httpUrl/public-message/bulk-delete",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun delete(id: UUID, userAccessToken: (suspend () -> String)?): Unit = fetch(
            url = "$httpUrl/public-message/${id.urlify()}",
            method = HttpMethod.DELETE,
            token = userAccessToken,
        )
        override suspend fun count(input: Condition<PublicMessage>, userAccessToken: (suspend () -> String)?): Int = fetch(
            url = "$httpUrl/public-message/count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupCount(input: GroupCountQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Map<String, Int> = fetch(
            url = "$httpUrl/public-message/group-count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun aggregate(input: AggregateQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Double? = fetch(
            url = "$httpUrl/public-message/aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupAggregate(input: GroupAggregateQuery<PublicMessage>, userAccessToken: (suspend () -> String)?): Map<String, Double?> = fetch(
            url = "$httpUrl/public-message/group-aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun watch(userToken: String?): TypedWebSocket<Query<PublicMessage>, ListChange<PublicMessage>> = multiplexedSocket(
            socketUrl = socketUrl, 
            path = "/public-message/rest", 
            token = userToken,
        )
    }
    class LiveNotificationApi(val httpUrl: String, val socketUrl: String): Api.NotificationApi {
        override suspend fun default(userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification/_default_",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun query(input: Query<Notification>, userAccessToken: suspend () -> String): List<Notification> = fetch(
            url = "$httpUrl/notification/query",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun queryPartial(input: QueryPartial<Notification>, userAccessToken: suspend () -> String): List<Partial<Notification>> = fetch(
            url = "$httpUrl/notification/query-partial",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun detail(id: UUID, userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification/${id.urlify()}",
            method = HttpMethod.GET,
            token = userAccessToken,
        )
        override suspend fun insertBulk(input: List<Notification>, userAccessToken: suspend () -> String): List<Notification> = fetch(
            url = "$httpUrl/notification/bulk",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun insert(input: Notification, userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun upsert(id: UUID, input: Notification, userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification/${id.urlify()}",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkReplace(input: List<Notification>, userAccessToken: suspend () -> String): List<Notification> = fetch(
            url = "$httpUrl/notification",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun replace(id: UUID, input: Notification, userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification/${id.urlify()}",
            method = HttpMethod.PUT,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkModify(input: MassModification<Notification>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/notification/bulk",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modifyWithDiff(id: UUID, input: Modification<Notification>, userAccessToken: suspend () -> String): EntryChange<Notification> = fetch(
            url = "$httpUrl/notification/${id.urlify()}/delta",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun modify(id: UUID, input: Modification<Notification>, userAccessToken: suspend () -> String): Notification = fetch(
            url = "$httpUrl/notification/${id.urlify()}",
            method = HttpMethod.PATCH,
            token = userAccessToken,
            body = input
        )
        override suspend fun bulkDelete(input: Condition<Notification>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/notification/bulk-delete",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun delete(id: UUID, userAccessToken: suspend () -> String): Unit = fetch(
            url = "$httpUrl/notification/${id.urlify()}",
            method = HttpMethod.DELETE,
            token = userAccessToken,
        )
        override suspend fun count(input: Condition<Notification>, userAccessToken: suspend () -> String): Int = fetch(
            url = "$httpUrl/notification/count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupCount(input: GroupCountQuery<Notification>, userAccessToken: suspend () -> String): Map<String, Int> = fetch(
            url = "$httpUrl/notification/group-count",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun aggregate(input: AggregateQuery<Notification>, userAccessToken: suspend () -> String): Double? = fetch(
            url = "$httpUrl/notification/aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun groupAggregate(input: GroupAggregateQuery<Notification>, userAccessToken: suspend () -> String): Map<String, Double?> = fetch(
            url = "$httpUrl/notification/group-aggregate",
            method = HttpMethod.POST,
            token = userAccessToken,
            body = input
        )
        override suspend fun watch(userToken: String): TypedWebSocket<Query<Notification>, ListChange<Notification>> = multiplexedSocket(
            socketUrl = socketUrl, 
            path = "/notification/rest", 
            token = userToken,
        )
    }
}

