package kurasava.ep.epmodpack

import javafx.scene.image.Image
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.io.path.outputStream

object Downloader {

    private val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    fun download(url: URL, directory: Path): CompletableFuture<Path> = CompletableFuture.supplyAsync({
        val file = directory.resolve(url.toURI().path.substring(url.toURI().path.lastIndexOf("/") + 1))
        this.readUrl(url).use { input -> file.outputStream().use { output -> input.copyTo(output) } }
        return@supplyAsync file
    }, this.threadPool)

    fun downloadMod(mod: Mod, version: String, directory: Path): CompletableFuture<Path> {
        val url = URI.create(mod.versions[version]).toURL()
        return this.download(url, directory)
    }

    fun downloadModIcons(): HashSet<Pair<String, Image>> {
        val modIcons = HashSet<Pair<String, Image>>()
        for (mod in App.optionalMods) {
            CompletableFuture.supplyAsync({
                modIcons.add(
                    Pair(mod.id, Image(
                        this.readUrl(URI.create("https://raw.githubusercontent.com/Kurasava/ep-modpack/meta/icons/${mod.id}.png").toURL()))
                    )
                )
            }, this.threadPool)
        }
        return modIcons
    }

    fun readUrl(url: URL): InputStream {
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.connect()
        val responseCode = connection.responseCode
        if (responseCode in 200..299) return connection.inputStream
        throw IOException("HTTP request to $url failed: $responseCode")
    }
}
