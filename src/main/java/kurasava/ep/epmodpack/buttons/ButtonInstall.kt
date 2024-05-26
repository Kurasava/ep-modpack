package kurasava.ep.epmodpack.buttons

import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.Pane
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.controllers.ControllerMods
import kurasava.ep.epmodpack.Mod
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.CountDownLatch
import kotlin.io.path.Path


@OptIn(DelicateCoroutinesApi::class)
class ButtonInstall(
    private val buttonInstall: Button,
    private val checkBoxAddServers: CheckBox,
    private val versionText: Button,
    private val directoryToMods: TextField,
    private val buttonSelectDirectory: Button,
    private val buttonVersions: Button,
    private val buttonMods: Button,
    private val hideApp: Button,
    private val closeApp: Button,
    private val main: Pane
) {

    private val semaphore = Semaphore(Runtime.getRuntime().availableProcessors())


    init {
        buttonInstall.setOnMouseClicked {
            buttonInstall.isDisable = true
            checkBoxAddServers.isDisable = true
            directoryToMods.isDisable = true
            buttonVersions.isDisable = true
            buttonMods.isDisable = true
            buttonSelectDirectory.isDisable = true
            closeApp.isDisable = true
            hideApp.isDisable = true
            versionText.isDisable = true
            main.requestFocus()
            val version = versionText.text
            val directory = Path(this.directoryToMods.text)
            val addServers = checkBoxAddServers.isSelected
            GlobalScope.launch(Dispatchers.IO) {
                installMods(version, directory, addServers)
            }
        }
    }

    private fun installMods(version: String, directory: Path, addServers: Boolean) {
        val modsDir = directory.resolve("mods")
        if (Files.notExists(modsDir)) {
            Files.createDirectories(modsDir)
        }
        val mods1 = App.MODS
            .map { it as JSONObject }
            .filter { it.getBoolean("required") }
            .map { Mod(it.getString("id")) }.toHashSet()
        mods1.addAll(ControllerMods.getSelectedMods())
        Platform.runLater {
           App.stage.height += 35
            buttonInstall.layoutY += 35
        }

        val bar = ProgressBar().apply {
            layoutX = 87.0
            layoutY = 248.0
            styleClass.add("progress-bar")
            progress = 0.0
        }

        Platform.runLater {
            main.children.add(bar)
        }

        val objectSize = if (addServers) 1.0 / (mods1.size + 1) else 1.0 / mods1.size
        val latch = CountDownLatch(mods1.size)

        mods1.forEach { mod ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    download(mod, version, modsDir)
                } finally {
                    latch.countDown()
                    Platform.runLater {
                        bar.progress = (mods1.size - latch.count) * objectSize
                        println(bar.progress)
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            latch.await()
            if (addServers) {
                Platform.runLater {
                    bar.progress += objectSize
                }
                CheckBoxAddServers.addServers(directory)
            }
            Platform.runLater {
                main.children.remove(bar)
                App.stage.height -= 35
                buttonInstall.layoutY -= 35
                buttonInstall.isDisable = false
                buttonInstall.isDisable = false
                checkBoxAddServers.isDisable = false
                directoryToMods.isDisable = false
                buttonVersions.isDisable = false
                buttonMods.isDisable = false
                buttonSelectDirectory.isDisable = false
                closeApp.isDisable = false
                versionText.isDisable = false
                hideApp.isDisable = false
            }
        }
    }


    private suspend fun download(mod: Mod, version: String, directory: Path) {
        semaphore.withPermit {
            val url = URL(mod.versions[version])
            val modFile = directory.resolve(url.toURI().path.substring(url.toURI().path.lastIndexOf("/") + 1))
            try {
                val stream = openUrl(url)
                Files.createDirectories(directory.parent)
                Files.copy(stream, modFile, StandardCopyOption.REPLACE_EXISTING)
                if (mod.dependencies.isNotEmpty()) mod.dependencies.forEach { download(it, version, directory) }
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