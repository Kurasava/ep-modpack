package kurasava.ep.epmodpack

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import kurasava.ep.epmodpack.windows.MainWindow
import kurasava.ep.epmodpack.windows.ModsWindow
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import java.util.*

class App : Application() {

    @Throws(IOException::class)
    override fun start(mainStage: Stage) {
        stage = mainStage
        val image = Image("images/icon-24px.png")
        stage.icons.add(image)
        Font.loadFont(this::class.java.classLoader.getResource("Inter-Regular.ttf")!!.toExternalForm(), 10.0)
        stage.title = "Modpack installer"
        mainScene.stylesheets.add(this::class.java.getResource(STYLESHEET)!!.toExternalForm())
        modsScene.stylesheets.add(this::class.java.getResource(STYLESHEET)!!.toExternalForm())
        stage.scene = mainScene
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
    }

    companion object {
        private val modsUrl = URL("https://raw.githubusercontent.com/Kurasava/ep-modpack/meta/mods.json")
        private val serversUrl = URL("https://raw.githubusercontent.com/Kurasava/ep-modpack/meta/servers.json")
        private val versionsUrl = URL("https://raw.githubusercontent.com/Kurasava/ep-modpack/meta/versions.json")
        const val STYLESHEET = "/style.css"
        val mods = JSONArray(Downloader.readUrl(modsUrl).bufferedReader().readText())
        val servers = JSONArray(Downloader.readUrl(serversUrl).bufferedReader().readText())
        val versions = JSONArray(Downloader.readUrl(versionsUrl).bufferedReader().readText())
        lateinit var stage: Stage
        val mainScene = Scene(MainWindow.mainPane, Color.TRANSPARENT)
        val modsScene = Scene(ModsWindow.mods, Color.TRANSPARENT)
    }
}

fun main() {
    Application.launch(App::class.java)
}
