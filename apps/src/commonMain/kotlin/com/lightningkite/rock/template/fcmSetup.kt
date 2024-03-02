package com.lightningkite.rock.template


expect suspend fun fcmSetup(): Unit

expect suspend fun requestNotificationPermissions():Unit

expect suspend fun notificationPermissions():Boolean?
