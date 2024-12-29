package kurasava.ep.epmodpack

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import kurasava.ep.epmodpack.windows.MainWindow
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

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
        const val STYLESHEET = "/style.css"
        lateinit var stage: Stage
        val mods = JSONArray(JSONTokener(this.javaClass.getResourceAsStream("/mods.json")))
        val servers = JSONArray(JSONTokener(this.javaClass.getResourceAsStream("/servers.json")))
        val versions = JSONArray(JSONTokener(this.javaClass.getResourceAsStream("/versions.json")))
        val optionalMods = this.mods
            .map { it as JSONObject }
            .filter { !it.getBoolean("required") }
            .filter { !it.getBoolean("hidden") }
            .map { Mod(it.getString("id")) }
        val mainScene = Scene(MainWindow.mainPane, Color.TRANSPARENT)
    }

}

fun main() {
    Application.launch(App::class.java)
}
