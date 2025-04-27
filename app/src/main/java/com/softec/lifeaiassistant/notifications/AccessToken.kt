package com.softec.lifeaiassistant.notifications

import android.os.AsyncTask
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class AccessToken {
    companion object {
        private const val FIREBASE_MESSAGING_SCOPE =
            "https://www.googleapis.com/auth/firebase.messaging"

        fun getAccessTokenAsync(callback: AccessTokenCallback) {
            AccessTokenTask(callback).execute()
        }
    }

    private class AccessTokenTask(private val callback: AccessTokenCallback) :
        AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val jsonString = "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"life-ai-assistant\",\n" +
                        "  \"private_key_id\": \"50bb45f320f2c5c224d418653c0f9bd6e44bcb29\",\n" +
                        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC1wmmSvWhQW8U2\\n3vBSPWORsBouD1tn45TwD2aGgPYDZye+au4BN7UDjQsZFC7IhUVV/YVwNDkYYMHy\\n9mk18lpFumv7O179oyRaNUyGJ7oPbXcm7TKYf8w5yqfcyOOJaRfPMG4pSPMWbNgj\\n7RfDCvt/rkdO6ALsyfOC9IuGCgMFAzSrFYOuyrCNNsaFs5W5tZnckj+G5O/KJjYG\\nGRfsELy+xFS7TzOIqdA1g8vsox/ATuHIPc9/ULzM2+ZswyXVGGIqOjeESTs9NrZG\\nzOPoYiZrsINTwzMa6wDGZPmPsMZg1s+9KqjwjAVqEWmPjwEL7GCA9e+sjENLZ7yh\\nvIKdY7TRAgMBAAECggEAHLmwgLHxif2u8eiS95RfJ5e603u22PjlQXlVnT1FZC1J\\n4TS6D7Qzpe+FCP4hFKzHU/tAuwZt3ZXxIQpjf1ShgrKfPTHZZeGCLcWNpggiRizp\\nL1w+ak6MQrfuBp0zJLTYv/Rp/JlcXbdvA2KFplwWekvbVjOhvrV+CDzIzjLH3mMp\\n99B4Ie4nlPkd8rgeqzrU/P3bRXNGrKu7nBuubHYi/nqzPNxY245crTUX9JVwwtcX\\n4/gyh2VZzBPzn4wzQ79fDfLlrZG5WihZh3vdyH4lXqcDT5D6ZaQIOkaLVQEYFk7v\\nBREgbt/oogaFN8XBVZfYCIBWwQ7rGQKpHGt+quBinwKBgQD39Qj2ddzrf7rYhWau\\n9tUZv9gmZZBTv4Ygmn/RMO5BXE0alXT4Yt9HjzM5Zurnf4dCXVaPZFcpKG5yF/og\\nHuhyVqb8AJk8ww+IWm8ue48FWNUuAkP4EG1yvuU3xG/GNX+Po4KyL1+g2m0zE1st\\nXXjN2L9K9EBMCffQtBAXbBvZEwKBgQC7p7C7SKqA991dF9D9Ek2UsWZdafuwcjny\\n2JQVxtL1RUiB/zTPq7lM+4ri+28ZooY61StU7TcqSlrZsXBhseuZ1IpOharOOKl3\\nm2KoYNn0Tg4wKuuAbebJv7Qds14KR9zu6ChtZKnzrteUb/Gb6aSOwXbjKxzKbyf4\\nHJH7fW47CwKBgHpv03onnuvmiXOrA5Y3qehqE6h6FDdWZjupLp40RCGgGy6aSETy\\nRYCHVGrux+WB0l9RyZFKocNgBJFwB4Fe4ba71P6wqPoY842H4kiYmHmycKmkUr2m\\nFvVHD9ZmuC6ucguSWNH1lInP8YX789TFyAYVFfhD/Y/OiEDU+PEn3+RjAoGBAKON\\nKLRiRfC3lyBO5J/c9u70mrIp7+o99BGa/53nNu/8HZDnfADPu2XECq1/1Lnz86eN\\nKQIQ6babLYl0Y+1R43SkUJLVXyPzMIiFTjxQhUWvTiBfF/hMUQxfX71PGlQ9g+pZ\\nPvrw9ErlhRDjbCGmRf0B0vt1FNe+f4h4ZidZj9w3AoGBAIn6s378/DJZE0r/p1+f\\nhvyPTWFLQcRv0SDcigstSOZr48ceqH4/g2QwgsdCRMvzJh9NbTI96508DdsXV0co\\n+ku7dYOmFbi4F4O0CWj0tsBwF92m8K9Wa5b2buLDhX//zw40irOowj3pWXztp0yT\\n1DIQTkX9dVVwp2u1OT7NtidI\\n-----END PRIVATE KEY-----\\n\",\n" +
                        "  \"client_email\": \"firebase-adminsdk-fbsvc@life-ai-assistant.iam.gserviceaccount.com\",\n" +
                        "  \"client_id\": \"112942322990335543527\",\n" +
                        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40life-ai-assistant.iam.gserviceaccount.com\",\n" +
                        "  \"universe_domain\": \"googleapis.com\"\n" +
                        "}\n"
                val stream: InputStream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
                val googleCredentials = GoogleCredentials
                    .fromStream(stream)
                    .createScoped(Lists.newArrayList(FIREBASE_MESSAGING_SCOPE))
                googleCredentials.refreshIfExpired()
                googleCredentials.accessToken.tokenValue
            } catch (e: IOException) {
                Log.e("AccessToken", "Error retrieving access token", e)
                null
            }
        }

        override fun onPostExecute(token: String?) {
            callback.onAccessTokenReceived(token)
        }
    }

    interface AccessTokenCallback {
        fun onAccessTokenReceived(token: String?)
    }
}
