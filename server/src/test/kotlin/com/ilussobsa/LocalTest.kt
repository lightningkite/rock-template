package com.lightningkite.rock.template

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.lightningkite.lightningdb.insertOne
import com.lightningkite.lightningdb.modification
import com.lightningkite.lightningserver.exceptions.NotFoundException
import com.lightningkite.lightningserver.notifications.FcmNotificationClient
import com.lightningkite.lightningserver.notifications.NotificationSettings
import com.lightningkite.lightningserver.typed.test
import com.lightningkite.uuid
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.fail

class LocalTest {
    @Test fun testFcm(): Unit = runBlocking{
        FcmNotificationClient
        NotificationSettings(
            implementation = "fcm",
            credentials = "{\n  \"type\": \"service_account\",\n  \"project_id\": \"ilusso-bsa\",\n  \"private_key_id\": \"980dc7424c618b565bef264979a4a92c5f95df9a\",\n  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDciGA6TQghLBsx\\nWDRPPKKxAT0oeSBUA3qO1l3TeuWjdBHQIkuCyZDn4WamkM7FnmiUGBnmJ43gyYDJ\\nj05Tc3N6Q9eEzdGVuM9HIYphYrcADWkFdymPgjGaykoUdwbuBAqR4agIXRm+zCtL\\nMWcaYp71o6x3OK0ueK0CB4LumV17JzEmDbfCibagBNRfnai2AUGw4z2SNtFl7U0g\\nKnUpG4wdjo6LC9xlYdmr8DZcg8zxf5SS27/q68Gf83yvJfyl650k4NVUCtF5RH47\\nGT8txvbEu+miq7KbiAbb4SxoLUZrVQP5skVCOX+82+vLqIjaFJBHwdBDPnz8S3XK\\nU6+oQ69TAgMBAAECggEAJTSKy3swemw3ADmpxY5swiD74OaiehoGJK/sr1+F+2/7\\nC1ql06BO5pfj2gkHIVbqvMVeJTKaIIjORfL7219YZZGpe/m/OJuvuIfjkS92wTcB\\n906Vv+TOmpczLUWxcRlUcS0ZTKPsUoCeczaX9t8Zg0aEM67npLXuNi/vOnK8TyjK\\nr+5EbEpJKIEbQQCMpIXBmG8C5Hs2ZqTs60/Ggz6yZh+cr4gTGyqtSNXChWZFPNNb\\n2P1OHxLyazZ93d2nyxq0Bh9bt7APmN+KectLgqTjLSpZBOtXbGIkYW5mvDCoUD7R\\nvpA9aTJ+a0ln2cR9PBV6b8KJvGetLrEXGRD6F6EntQKBgQD9LgCN8FcjPq7DtNcM\\nxELJ/kLuZce4x9h/5A/cKaLNsX5GNspF3Ccc3GONTIllurX+7d78dvPZkLtyUDQ9\\nOApA535QaYgBWEoiD+E92yYZyu9Di7WKKHqxLBjCCFbgT5Ll7I4fUbSN8zwGLOKs\\nx1rhou1EIPLQcxzko0pLfseb3wKBgQDe/UYOPlsPsqVuK9P9G4n5pOHH2fI1ELVK\\nce6WGm3tfIOdtJfYJflgIDRmxPbzOQruoMgKKuG/myamcT80gxDtRITZMeBKNXFW\\n3tP/MavalADPXeKG+Dg7i1gF62NQFHBHz9p8J3cqT6SSIppOq9Msj2/OKImetFuA\\nd2/AI1fbDQKBgQD5ox5Q1iZVTj0sI2fX4ryoRtjgD+N6QzMqB0nMIBGzF8wWl3ei\\nooy+XGCctlbTw+In14wDC5ctcxQghwy7QEJ79MYLq8K68L9IDrhdMXTEAeEhjOQX\\nsgooH/7ZczeWPV/u7ezE3N9XB/qrbB6llnv595hTkNlax+Y5Wviqr/tJqQKBgB3U\\nZc27+7SC4mx1gntGdtOlgeqZNjRxEr9ttEvyJyhw0q6DHRj2I4D/beMdkCeDRvob\\nGgd6Kw7Vs3rqhVkB+36x/s+LQ4TJ12qWjmeB/opT6DPVTFtf0y3r+w93qKZqxYZl\\nwMWBBgjr9Ij0Bg5mH+7DFWLPu3E2kVMDJzjdwX/1AoGBAOpqtlLg83VUoKSiSH/6\\nSRkrRbMzq3qVf1zXgndGvOfap8ce/fb4teUwkOHhtB+KW7IgL+vXlAu0QPT1wYZZ\\nQa4NzHPPKGopOnv7CXFqImt3uOdnaB5dlGCLRgwXbgW8+UDDphGZZPipnluKXQg4\\nmF55+xtiSPTqrXq32qt38ABV\\n-----END PRIVATE KEY-----\\n\",\n  \"client_email\": \"firebase-adminsdk-d98al@ilusso-bsa.iam.gserviceaccount.com\",\n  \"client_id\": \"100390851663584261758\",\n  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-d98al%40ilusso-bsa.iam.gserviceaccount.com\",\n  \"universe_domain\": \"googleapis.com\"\n}\n"
        ).invoke().send(
            listOf("eurV5yPSo2kzCvTXCF6BPM:APA91bG8HHbbicgjhSmdJccaKeMaksvo1XLuXTeNwkVIAbbKK21bH8ohGcFAsCgvCgBEvfjxYwS3rT1AQIPCzwFVN0LL6abuQGML53qw4Z_RH9VL87r6IZZZOS0CQJNo-ajimX20Th2N"),
            title = "HEY",
            body = "gotta get up MOVE"
        )
    }
}
