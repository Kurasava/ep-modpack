package kurasava.ep.epmodpack.buttons

import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import kurasava.ep.epmodpack.App
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ButtonModsLocation(
    button: Button,
    private val textFieldDirectory: TextField,
    private val main: Pane,
) {
    init {
        textFieldDirectory.text = getDefaultDir().toString()
        button.setOnMouseClicked {
            main.requestFocus()
            val directory = DirectoryChooser()
            directory.initialDirectory = File(textFieldDirectory.text)
            val selectedDirectory = directory.showDialog(App.stage)
            if (selectedDirectory != null) textFieldDirectory.text = selectedDirectory.absolutePath
        }
    }

    private fun getDefaultDir(): Path {
        val homeDir = Paths.get(System.getProperty("user.home", "."))
        var dir: Path = homeDir.resolve(".minecraft")
        when (OperatingSystem.CURRENT) {
            OperatingSystem.WINDOWS -> {
                if (System.getenv("APPDATA") != null) {
                    dir = Paths.get(System.getenv("APPDATA")).resolve(".minecraft")
                }
            }
            OperatingSystem.MACOS -> {
                dir = homeDir.resolve("Library").resolve("Application Support").resolve("minecraft")
            }
            OperatingSystem.LINUX -> {
                val flatPack = homeDir.resolve(".var")
                    .resolve("app")
                    .resolve("com.mojang.Minecraft")
                    .resolve(".minecraft")

                if (Files.exists(flatPack)) {
                    dir = flatPack
                }
            }
        }
        return dir.toAbsolutePath().normalize()
    }

    enum class OperatingSystem {
        WINDOWS,
        MACOS,
        LINUX,
        ;

        companion object {
            val CURRENT: OperatingSystem = current

            private val current: OperatingSystem
                get() {
                    val osName = System.getProperty("os.name").lowercase()

                    return if (osName.contains("win")) {
                        WINDOWS
                    } else if (osName.contains("mac")) {
                        MACOS
                    } else {
                        LINUX
                    }
                }
        }
    }
}
