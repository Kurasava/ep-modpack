package kurasava.ep.epmodpack

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

class Url {
    companion object {
        private val semaphore = Semaphore(Runtime.getRuntime().availableProcessors())

        suspend fun downloadMod(mod: Mod, version: String, directory: Path) {
            semaphore.withPermit {
                val url = URL(mod.versions[version])
                val modFile = directory.resolve(url.toURI().path.substring(url.toURI().path.lastIndexOf("/") + 1))
                try {
                    val stream = openUrl(url)
                    Files.createDirectories(directory.parent)
                    Files.copy(stream, modFile, StandardCopyOption.REPLACE_EXISTING)
                    if (mod.dependencies.isNotEmpty()) mod.dependencies.forEach { downloadMod(it, version, directory) }
                } catch (t: Throwable) {
                    try {
                        Files.deleteIfExists(modFile)
                    } catch (t2: Throwable) {
                        t.addSuppressed(t2)
                    }

                    throw t
                }
            }
        }

        private fun openUrl(url: URL): InputStream {
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode in 200..299) return connection.inputStream
            throw IOException("HTTP request to $url failed: $responseCode")
        }
    }
}