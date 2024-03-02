package com.lightningkite.rock.template

import com.lightningkite.rock.navigation.PlatformNavigator
import com.lightningkite.rock.suspendCoroutineCancellable
import kotlinx.browser.window
import org.w3c.dom.MODULE
import org.w3c.dom.WorkerType
import org.w3c.notifications.*
import org.w3c.workers.RegistrationOptions
import kotlin.coroutines.resume
import kotlin.js.json

fun onNotification(notification: MessagePayload) {
    window.navigator.serviceWorker
        .register(PlatformNavigator.basePath + "firebase-messaging-sw.js", RegistrationOptions(scope = PlatformNavigator.basePath, type = WorkerType.MODULE))
        .then { registration ->
            notification.notification
            window.setTimeout(
                {
                    registration.showNotification(
                        notification.notification?.title ?: "Notification",
                        NotificationOptions(
                            body = notification.notification?.body,
                            image = notification.notification?.image,
                            icon = notification.notification?.icon,
                            data = json("link_url" to notification.fcmOptions?.link)
                        )
                    )
                        .catch { e ->
                            console.error("Error sending notificaiton to user", e);
                        }
                    registration.update()
                },
                100
            )
        }.catch { }
}

//private var alreadySetup = false
actual suspend fun fcmSetup(): Unit {
//    if (!alreadySetup) {
        try {
            val firebaseAppOptions: dynamic = object {}
            firebaseAppOptions["apiKey"] = ""
            firebaseAppOptions["authDomain"] = ""
            firebaseAppOptions["projectId"] = ""
            firebaseAppOptions["storageBucket"] = ""
            firebaseAppOptions["messagingSenderId"] = ""
            firebaseAppOptions["appId"] = ""

            val app = initializeApp(firebaseAppOptions.unsafeCast<FirebaseOptions>())
            val messaging = getMessaging(app)

            window.navigator.serviceWorker
                .register(PlatformNavigator.basePath + "firebase-messaging-sw.js", RegistrationOptions(scope = PlatformNavigator.basePath, type = WorkerType.MODULE))
                .then { serviceWorker ->
                    val messagingOptions: dynamic = object {}
                    messagingOptions["vapidKey"] = ""
                    messagingOptions["serviceWorkerRegistration"] = serviceWorker
                    getToken(
                        messaging,
                        messagingOptions.unsafeCast<GetTokenOptions>()
                    ).then {
                        fcmToken.value = it
                    }.catch {
                        it.printStackTrace()
                    }

                    onMessage(messaging) { messagePayload ->
                        println("Received payload $messagePayload")
                        onNotification(messagePayload)
                    }

                }.catch {
                    it.printStackTrace()
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
//        alreadySetup = true
//    } else {
//        fcmToken.value = fcmToken.value
//    }
}

actual suspend fun requestNotificationPermissions(): Unit {
    val result = suspendCoroutineCancellable { cont ->
        org.w3c.notifications.Notification.requestPermission { result ->
            cont.resume(result)
        }
        return@suspendCoroutineCancellable {}
    }
    if (result == NotificationPermission.GRANTED) fcmSetup()
}

actual suspend fun notificationPermissions(): Boolean? {
    return if (js("'Notification' in window").unsafeCast<Boolean>())
        when (org.w3c.notifications.Notification.permission) {
            NotificationPermission.GRANTED -> true
            NotificationPermission.DENIED -> false
            else -> null
        } else false
}
