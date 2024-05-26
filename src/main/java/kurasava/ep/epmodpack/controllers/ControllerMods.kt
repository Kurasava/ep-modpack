package kurasava.ep.epmodpack.controllers

import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.layout.Pane
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.Mod
import kotlin.collections.HashSet

class ControllerMods : Controller() {

    lateinit var backToMain: Button
    lateinit var content: Pane

    override fun initialize() {
        super.initialize()
        contentInstance = content
        backToMain.setOnMouseClicked {
            App.stage.scene = App.scene
            contentInstance = content
        }
    }

    companion object {
        lateinit var contentInstance: Pane
        fun getSelectedMods(): HashSet<Mod> {
            return contentInstance.children
                .filterIsInstance<CheckBox>()
                .filter { it.isSelected }
                .map { Mod(it.text) }.toHashSet()
        }
    }
}