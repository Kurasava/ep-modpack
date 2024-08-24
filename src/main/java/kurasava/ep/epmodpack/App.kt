package kurasava.ep.epmodpack

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import kurasava.ep.epmodpack.windows.MainWindow
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
        stage.title = "Modpack installer"
        mainScene.stylesheets.add(this::class.java.getResource(STYLESHEET)!!.toExternalForm())
        stage.scene = mainScene
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.resizableProperty().set(true)
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
        val optionalMods = this.mods
            .map { it as org.json.JSONObject }
            .filter { !it.getBoolean("required") }
            .filter { !it.getBoolean("hidden") }
            .map { Mod(it.getString("id")) }
        val modImages = Downloader.downloadModIcons()
        lateinit var stage: Stage
        val mainScene = Scene(MainWindow.mainPane, Color.TRANSPARENT)
    }
}

fun main() {
    Application.launch(App::class.java)
}
