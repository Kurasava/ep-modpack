package kurasava.ep.epmodpack.controllers

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import kurasava.ep.epmodpack.buttons.*

class MainController : Controller() {
    @FXML
    lateinit var checkBoxAddServers: CheckBox

    @FXML
    lateinit var buttonSelectDirectory: Button

    @FXML
    lateinit var buttonMods: Button

    @FXML
    lateinit var buttonVersions: Button

    @FXML
    lateinit var versionText: Button

    @FXML
    lateinit var hideApp: Button

    @FXML
    lateinit var closeApp: Button

    @FXML
    lateinit var textFieldDirectory: TextField

    @FXML
    lateinit var main: Pane

    @FXML
    lateinit var header: Pane

    @FXML
    lateinit var buttonInstall: Button

    override fun initialize() {
        super.initialize()

        ButtonVersions(versionText, buttonVersions, main)
        ButtonMods(buttonMods, main)
        ButtonModsLocation(buttonSelectDirectory, textFieldDirectory, main)
        CheckBoxAddServers(checkBoxAddServers, main)
        ButtonInstall(
            buttonInstall,
            checkBoxAddServers,
            versionText,
            textFieldDirectory,
            buttonSelectDirectory,
            buttonVersions,
            buttonMods,
            hideApp,
            closeApp,
            main,
        )

        main.setOnMouseClicked {
            main.requestFocus()
        }

        header.setOnMouseClicked {
            main.requestFocus()
        }
    }
}
