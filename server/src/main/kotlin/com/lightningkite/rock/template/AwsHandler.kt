package com.lightningkite.rock.template

import com.lightningkite.lightningserver.aws.AwsAdapter
import com.lightningkite.lightningserver.serialization.Serialization
import com.lightningkite.lightningserver.settings.Settings
import kotlinx.serialization.decodeFromString
import org.crac.Context
import org.crac.Resource
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3Client

class AwsHandler: AwsAdapter() {
    companion object {
        val log = LoggerFactory.getLogger("AwsHandler")
        init {
            log.info("Starting up...")
            Server
            log.info("Server assembled...")
            loadSettings(AwsHandler::class.java)
            log.info("Handler ready.")
        }
    }
    init {
        Companion
        println("Init complete")
    }

    override fun afterRestore(context: Context<out Resource>?) {
        println("AwsHandler: afterRestore")
        super.afterRestore(context)
    }

    override fun beforeCheckpoint(context: Context<out Resource>?) {
        println("AwsHandler: beforeCheckpoint")
        super.beforeCheckpoint(context)
    }
}