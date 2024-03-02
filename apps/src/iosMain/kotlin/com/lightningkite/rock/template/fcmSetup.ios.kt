package com.lightningkite.rock.template

import com.lightningkite.rock.suspendCoroutineCancellable
import platform.UIKit.UIDevice
import platform.UserNotifications.*
import kotlin.coroutines.resume

actual suspend fun fcmSetup() {
}

actual suspend fun requestNotificationPermissions() {
    val result = suspendCoroutineCancellable { cont ->
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings: UNNotificationSettings? ->
                when {
                    settings == null -> cont.resume(UNAuthorizationStatusDenied)
                    settings.authorizationStatus == UNAuthorizationStatusNotDetermined -> {
                        val options = listOf(UNAuthorizationOptionSound, UNAuthorizationOptionBadge)
                        UNUserNotificationCenter.currentNotificationCenter()
                            .requestAuthorizationWithOptions(options.reduce { acc, it -> acc or it }) { approved, e ->
                                cont.resume(
                                    if (approved) {
                                        UNAuthorizationStatusAuthorized
                                    } else UNAuthorizationStatusDenied
                                )
                            }
                    }

                    else -> cont.resume(settings.authorizationStatus)
                }
            }
        return@suspendCoroutineCancellable {}
    }
    if (result == UNAuthorizationStatusAuthorized)
        fcmSetup()
}

actual suspend fun notificationPermissions(): Boolean? {
    return suspendCoroutineCancellable { cont ->
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler {
                cont.resume(it?.authorizationStatus?.let {
                    it >= UNAuthorizationStatusAuthorized
                })
            }
        return@suspendCoroutineCancellable { }
    }
}