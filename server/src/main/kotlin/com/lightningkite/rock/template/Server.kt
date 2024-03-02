package com.lightningkite.rock.template

import com.lightningkite.UUID
import com.lightningkite.lightningdb.*
import com.lightningkite.lightningserver.auth.*
import com.lightningkite.lightningserver.auth.oauth.OauthProviderCredentials
import com.lightningkite.lightningserver.auth.oauth.OauthProviderCredentialsApple
import com.lightningkite.lightningserver.auth.oauth.OauthProviderInfo
import com.lightningkite.lightningserver.auth.proof.*
import com.lightningkite.lightningserver.auth.subject.AuthEndpointsForSubject
import com.lightningkite.lightningserver.cache.CacheSettings
import com.lightningkite.lightningserver.core.ContentType
import com.lightningkite.lightningserver.core.ServerPath
import com.lightningkite.lightningserver.core.ServerPathGroup
import com.lightningkite.lightningserver.db.DatabaseSettings
import com.lightningkite.lightningserver.db.DynamoDbCache
import com.lightningkite.lightningserver.email.Email
import com.lightningkite.lightningserver.email.EmailLabeledValue
import com.lightningkite.lightningserver.email.EmailSettings
import com.lightningkite.lightningserver.email.SesClient
import com.lightningkite.lightningserver.exceptions.BadRequestException
import com.lightningkite.lightningserver.exceptions.NotFoundException
import com.lightningkite.lightningserver.exceptions.SentryExceptionReporter
import com.lightningkite.lightningserver.files.FilesSettings
import com.lightningkite.lightningserver.files.S3FileSystem
import com.lightningkite.lightningserver.files.UploadEarlyEndpoint
import com.lightningkite.lightningserver.http.HttpContent
import com.lightningkite.lightningserver.http.HttpResponse
import com.lightningkite.lightningserver.http.get
import com.lightningkite.lightningserver.http.handler
import com.lightningkite.lightningserver.meta.metaEndpoints
import com.lightningkite.lightningserver.metrics.CloudwatchMetrics
import com.lightningkite.lightningserver.notifications.FcmNotificationClient
import com.lightningkite.lightningserver.notifications.NotificationSettings
import com.lightningkite.lightningserver.serialization.Serialization
import com.lightningkite.lightningserver.settings.setting
import com.lightningkite.lightningserver.sms.SMSSettings
import com.lightningkite.lightningserver.websocket.MultiplexWebSocketHandler
import com.lightningkite.lightningserver.websocket.websocket
import com.lightningkite.uuid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

object Server : ServerPathGroup(ServerPath.root) {

    init {
        Serialization.json = Json {
            ignoreUnknownKeys = true
            serializersModule = Serialization.module
            encodeDefaults = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
        }
    }

    val database = setting("database", DatabaseSettings())
    val email = setting("email", EmailSettings())
    val sms = setting("sms", SMSSettings())
    val files = setting("files", FilesSettings())
    val notifications = setting("notifications", NotificationSettings())
    val cache = setting("cache", CacheSettings())
    val serveApp = setting<String?>("serveApp", null)
    val oauthApple = setting<OauthProviderCredentialsApple>("oauthApple", OauthProviderCredentialsApple("", "", "", ""))
    val oauthGoogle = setting<OauthProviderCredentials>("oauthGoogle", OauthProviderCredentials("", ""))
    val oauthMicrosoft = setting<OauthProviderCredentials>("oauthMicrosoft", OauthProviderCredentials("", ""))
    val frontend = setting<String>("frontend", "/app")

    init {
        listOf(
            SesClient,
            DynamoDbCache,
            CloudwatchMetrics,
            FcmNotificationClient,
            MongoDatabase,
            SentryExceptionReporter,
            S3FileSystem,
        )
        prepareModels()
        Authentication.isDeveloper = authRequired<User> {
            it.role() >= UserRole.Developer
        }
        Authentication.isSuperUser = authRequired<User> {
            it.role() >= UserRole.Root
        }
        Authentication.isAdmin = authRequired<User> {
            it.role() >= UserRole.Admin
        }
    }

    val root = get.handler {
        if (it.user<User?>() == null) HttpResponse.redirectToGet(auth.html.html0.path.toString())
        else HttpResponse.redirectToGet(meta.admin.path.toString() + "models/auction")
    }

    val users = UserEndpoints(path("users"))

    val pins = PinHandler(cache, "pins")
    val proofPhone = SmsProofEndpoints(path("proof/phone"), pins, sms)
    val proofEmail = EmailProofEndpoints(path("proof/email"), pins, email, { to, pin ->
        Email(
            subject = "Log In Code",
            to = listOf(EmailLabeledValue(to)),
            plainText = "Your PIN is $pin."
        )
    })
    val proofOtp = OneTimePasswordProofEndpoints(path("proof/otp"), database, cache)
    val proofPassword = PasswordProofEndpoints(path("proof/password"), database, cache)
    val proofApple = OauthProofEndpoints(path("proof/oauth/apple"), provider = OauthProviderInfo.apple, credentials = { oauthApple().toOauthProviderCredentials() }, continueUiAuthUrl = { frontend() + "/login" })
    val proofGoogle = OauthProofEndpoints(path("proof/oauth/google"), provider = OauthProviderInfo.google, credentials = oauthGoogle, continueUiAuthUrl = { frontend() + "/login" })
    val proofMicrosoft = OauthProofEndpoints(path("proof/oauth/microsoft"), provider = OauthProviderInfo.microsoft, credentials = oauthMicrosoft, continueUiAuthUrl = { frontend() + "/login" })
    val auth = AuthEndpointsForSubject(
        path("auth"),
        object : Authentication.SubjectHandler<User, UUID> {
            override val name: String get() = "User"
            override val authType: AuthType get() = AuthType<User>()
            override val idSerializer: KSerializer<UUID>
                get() = users.info.serialization.idSerializer
            override val subjectSerializer: KSerializer<User>
                get() = users.info.serialization.serializer

            override suspend fun fetch(id: UUID): User = users.info.collection().get(id) ?: throw NotFoundException()
            override suspend fun findUser(property: String, value: String): User? = when (property) {
                "email" -> users.info.collection().findOne(condition { it.email eq value }) ?: run {
                    users.info.collection().insertOne(User(email = value))!!
                }
                "_id" -> users.info.collection().get(uuid(value))
                else -> null
            }

            override val knownCacheTypes: List<RequestAuth.CacheKey<User, UUID, *>> = listOf(RoleCacheKey)

            override suspend fun desiredStrengthFor(result: User): Int =
                if (result.role >= UserRole.Admin) Int.MAX_VALUE else 5
        },
        database = database
    )

    val fcmTokens = FcmTokenEndpoints(path("fcmTokens"))
    val uploadEarly = UploadEarlyEndpoint(path("upload-early"), files, database)

    val multiplex = path("multiplex").websocket(MultiplexWebSocketHandler(cache))

    val meta = path("meta").metaEndpoints()

    val publicMessage = PublicMessageEndpoints(path("public-message"))
    val notification = NotificationEndpoints(path("notification"))

    val app = path("app/{...}").get.handler {
        val location = serveApp() ?: throw NotFoundException()
        if(it.wildcard?.contains("..") == true) throw BadRequestException()
        if(it.wildcard?.contains("~") == true) throw BadRequestException()
        if(it.wildcard?.startsWith("/") == true) throw BadRequestException()
        File(location)
            .resolve(it.wildcard ?: "")
            .takeIf { it.exists() && it.isFile }?.let {
                HttpResponse(HttpContent.file(it))
            }
            ?: run  {
                val inject = """
                    <script id="baseUrlLocation" type="application/json">{ "baseUrl": "/app/" }</script>
                """.trimIndent()
                val file = File(location).resolve("index.html")
                HttpResponse(HttpContent.Text(file.readText().let {
                    it.substringBefore("<head>") + "<head>" + inject + it.substringAfter("<head>").replace(
                        "<script src=\"/apps.js\"></script>",
                        "<script src=\"/app/apps.js\"></script>",
                    )
                }, ContentType.Text.Html))
            }
    }
}