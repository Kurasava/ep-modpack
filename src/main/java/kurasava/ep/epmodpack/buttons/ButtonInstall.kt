package kurasava.ep.epmodpack.buttons

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.Mod
import kurasava.ep.epmodpack.Url
import kurasava.ep.epmodpack.controllers.ControllerMods
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
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
                    Url.downloadMod(mod, version, modsDir)
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
}