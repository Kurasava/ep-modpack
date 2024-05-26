package kurasava.ep.epmodpack.buttons

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kurasava.ep.epmodpack.App

class ButtonMods(button: Button, private val main: Pane) {


    init {
        val modsWindow = FXMLLoader(this.javaClass.classLoader.getResource("optional_mods.fxml"))
        val scene = Scene(modsWindow.load())
        scene.fill = Color.TRANSPARENT
        button.setOnMouseClicked {
            main.requestFocus()
            App.stage.scene = scene
            App.stage.sizeToScene()
        }
    }
}