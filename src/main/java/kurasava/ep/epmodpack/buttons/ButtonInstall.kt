package kurasava.ep.epmodpack.buttons

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.util.Duration
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.Downloader
import kurasava.ep.epmodpack.Mod
import kurasava.ep.epmodpack.windows.ModsWindow
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

class ButtonInstall(
    private val buttonInstall: Button,
    private val checkBoxAddServers: CheckBox,
    private val versionText: Button,
    private val directoryToMods: TextField,
    private val buttonSelectDirectory: Button,
    private val buttonVersions: Button,
    private val buttonMods: Button,
    private val main: Pane,
) {
    init {
        buttonInstall.setOnMouseClicked {
            buttonInstall.isDisable = true
            buttonInstall.text = "Установка..."
            checkBoxAddServers.isDisable = true
            directoryToMods.isDisable = true
            buttonVersions.isDisable = true
            buttonMods.isDisable = true
            buttonSelectDirectory.isDisable = true
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
        mods.addAll(ModsWindow.getSelectedMods())

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
            layoutX = 50.0
            layoutY = 248.0
            styleClass.add("progress-bar")
            progress = 0.0
        }

        main.children.add(bar)

        val scheduler = Executors.newScheduledThreadPool(1)

        val objectSize = if (addServers) 1.0 / (selectedMods.size + 1) else 1.0 / selectedMods.size
        val latch = CountDownLatch(selectedMods.size)

        selectedMods.filter { it.isReleased(version) }.forEach { mod ->
            Downloader.downloadMod(mod, version, modsDir).whenComplete { _, _ ->
                latch.countDown()
                val targetProgress = (selectedMods.size - latch.count) * objectSize

                val timeline = Timeline(
                    KeyFrame(Duration.ZERO, KeyValue(bar.progressProperty(), bar.progress)),
                    KeyFrame(Duration.seconds(1.0), KeyValue(bar.progressProperty(), targetProgress)),
                )

                timeline.play()
            }
        }

        CompletableFuture.runAsync {
            latch.await()

            if (addServers) {
                bar.progress += objectSize
                CheckBoxAddServers.addServers(directory)
            }

            scheduler.schedule({
                Platform.runLater {
                    main.children.remove(bar)
                    App.stage.height -= 35
                    buttonInstall.layoutY -= 35
                    buttonInstall.isDisable = false
                    buttonInstall.text = "Установить"
                    checkBoxAddServers.isDisable = false
                    directoryToMods.isDisable = false
                    buttonVersions.isDisable = false
                    buttonMods.isDisable = false
                    buttonSelectDirectory.isDisable = false
                    versionText.isDisable = false
                }
            }, 2, TimeUnit.SECONDS)
        }
    }
}
