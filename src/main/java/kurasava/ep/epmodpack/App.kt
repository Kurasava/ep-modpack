package kurasava.ep.epmodpack

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException


class App : Application() {

    @Throws(IOException::class)
    override fun start(mainStage: Stage) {
        stage = mainStage
        val image = Image("images/icon-24px.png")
        stage.icons.add(image)
        stage.title = "Modpack installer"
        scene.fill = Color.TRANSPARENT
        stage.scene = scene
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
    }

    companion object {
        private val mainWindow = FXMLLoader(App::class.java.classLoader.getResource("main_window.fxml"))
        val scene = Scene(mainWindow.load())
        var MODS: JSONArray = readUrls("json/mods.json")
        lateinit var stage: Stage

        fun readUrls(path: String): JSONArray {
            val inputStream = App::class.java.classLoader.getResourceAsStream(path)
            val mods: JSONArray = try {
                JSONArray(JSONTokener(inputStream))
            } catch (ex: Throwable) {
                inputStream?.close()
                throw ex
            }
            inputStream?.close()
            return mods
        }
    }
}

fun main() {
    Application.launch(App::class.java)
}