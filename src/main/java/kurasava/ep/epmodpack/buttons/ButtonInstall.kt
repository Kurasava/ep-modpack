package kurasava.ep.epmodpack.buttons

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.Downloader
import kurasava.ep.epmodpack.Mod
import kurasava.ep.epmodpack.controllers.ControllerMods
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import kotlin.io.path.Path

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
    private val main: Pane,
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
            this.installMods(version, directory, addServers)
        }
    }

    private fun installMods(version: String, directory: Path, addServers: Boolean) {
        val modsDir = directory.resolve("mods")
        if (Files.notExists(modsDir)) {
            Files.createDirectories(modsDir)
        }

        val mods = App.mods
            .map { it as JSONObject }
            .filter { it.getBoolean("required") }
            .map { Mod(it.getString("id")) }
            .toHashSet()
        mods.addAll(ControllerMods.getSelectedMods())

        val stack = Stack<Mod>()
        stack.addAll(mods)
        val selectedMods = mutableSetOf<Mod>()

        while (stack.isNotEmpty()) {
            val mod = stack.pop()
            selectedMods.add(mod)
            mod.dependencies.forEach(stack::push)
        }

        App.stage.height += 35
        buttonInstall.layoutY += 35

        val bar = ProgressBar().apply {
            layoutX = 87.0
            layoutY = 248.0
            styleClass.add("progress-bar")
            progress = 0.0
        }

        main.children.add(bar)

        val objectSize = if (addServers) 1.0 / (selectedMods.size + 1) else 1.0 / selectedMods.size
        val latch = CountDownLatch(selectedMods.size)

        selectedMods.forEach { mod ->
            Downloader.downloadMod(mod, version, modsDir).whenComplete { _, _ ->
                latch.countDown()
                bar.progress = (selectedMods.size - latch.count) * objectSize
            }
        }

        CompletableFuture.runAsync {
            latch.await()

            if (addServers) {
                bar.progress += objectSize
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
