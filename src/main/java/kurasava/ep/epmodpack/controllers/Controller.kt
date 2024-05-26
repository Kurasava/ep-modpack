package kurasava.ep.epmodpack.controllers

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage

abstract class Controller {
    @FXML
    private lateinit var hideApp: Button

    @FXML
    private lateinit var closeApp: Button

    @FXML
    private lateinit var header: Pane

    private var xOffSet: Double = 0.0
    private var yOffSet: Double = 0.0

    open fun initialize() {
        hideApp.setOnMouseClicked { hideOrClose(it.button, true) }
        closeApp.setOnMouseClicked { hideOrClose(it.button, false) }
    }

    @FXML
    private fun getLocationWindow(e: MouseEvent) {
        this.xOffSet = e.sceneX
        this.yOffSet = e.sceneY
    }

    @FXML
    private fun setLocationWindow(e: MouseEvent) {
        val stage = header.scene.window as Stage
        stage.x = e.screenX - this.xOffSet
        stage.y = e.screenY - this.yOffSet
    }

    private fun hideOrClose(button: MouseButton, hide: Boolean) {
        if (button != MouseButton.PRIMARY) return
        val stage = header.scene.window as Stage
        if (hide) stage.isIconified = true else stage.close()
        stage.iconifiedProperty().addListener { _, _, isNowMinimized ->
            if (!isNowMinimized) {
                hideApp.style = ""
                hideApp.styleClass.add("button-hide")
            }
        }
    }
}