package kurasava.ep.epmodpack.windows

import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.text.TextAlignment
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.buttons.ButtonInstall
import kurasava.ep.epmodpack.buttons.ButtonModsLocation
import kurasava.ep.epmodpack.buttons.CheckBoxAddServers
import kurasava.ep.epmodpack.objects.Header
import java.util.stream.IntStream

object MainWindow {
    val mainPane = Pane()
    private val header = Header(true)
    private val versionLabel = Label()
    private val textVersionButton = Button()
    private val versionButton = Button()
    private val contextMenu = ContextMenu()
    private val modsLabel = Label()
    private val modsButton = Button()
    private val locationLabel = Label()
    private val locationTextField = TextField()
    private val locationButton = Button()
    private val serversLabel = Label()
    private val serversCheckBox = CheckBox()
    private val installButton = Button()

    init {
        this.initMods()
        this.initVersionsLabel()
        this.initTextVersionButton()
        this.initVersionButton()
        this.initModsLabel()
        this.initModsButton()
        this.initLocationLabel()
        this.initLocationTextField()
        this.initLocationButton()
        this.initServersLabel()
        this.initServersCheckBox()
        this.initInstallButton()
        this.initializeButtonActions()
        mainPane.children.addAll(
            header.header, versionLabel, textVersionButton,
            versionButton, modsLabel, modsButton, locationLabel, locationTextField,
            locationButton, serversLabel, serversCheckBox, installButton,
        )
    }

    private fun initMods() = mainPane.apply {
        prefHeight = 305.0
        prefWidth = 500.0
        styleClass.add("main-pane")
        focusTraversableProperty().set(true)
    }

    private fun initVersionsLabel() = versionLabel.apply {
        prefWidth = 128.0
        layoutX = 18.0
        layoutY = 73.0
        styleClass.add("label-versions")
        text = "Версия игры:"
        textAlignment = TextAlignment.CENTER
    }

    private fun initTextVersionButton() {
        contextMenu.styleClass.add("context-menu-versions")
        val versionsArray = App.versions
        val listVersions = mutableListOf<String>()
        IntStream.range(0, versionsArray.length()).forEach {
            listVersions.add(versionsArray.getString(it))
        }

        this.createContain(listVersions)
        textVersionButton.apply {
            prefHeight = 30.0
            prefWidth = 100.0
            layoutX = 123.0
            layoutY = 67.0
            text = listVersions.first()
            styleClass.add("button-versions-text")
            focusTraversableProperty().set(false)
        }
    }

    private fun initVersionButton() = versionButton.apply {
        prefHeight = 30.0
        prefWidth = 36.0
        layoutX = 230.0
        layoutY = 67.0
        styleClass.add("button-versions")
        focusTraversableProperty().set(false)
        mnemonicParsingProperty().set(false)
        text = "▼"
        setOnMouseClicked {
            mainPane.requestFocus()
            this@MainWindow.openMenuVersions()
        }

        scaleble(this)
    }

    private fun initModsLabel() = modsLabel.apply {
        prefHeight = 12.0
        prefWidth = 170.0
        layoutX = 18.0
        layoutY = 122.0
        styleClass.add("label-mods")
        text = "Опциональные моды:"
    }

    private fun initModsButton() = modsButton.apply {
        prefHeight = 30.0
        prefWidth = 36.0
        layoutX = 185.0
        layoutY = 116.0
        styleClass.add("button-mods")
        text = "..."
        textAlignment = TextAlignment.CENTER
        setOnMouseClicked {
            mainPane.requestFocus()
            App.stage.scene = App.modsScene
            App.stage.sizeToScene()
        }

        scaleble(this)
    }

    private fun initLocationLabel() = locationLabel.apply {
        prefHeight = 10.0
        prefWidth = 120.0
        layoutX = 18.0
        layoutY = 170.0
        styleClass.add("label-location")
        text = "Расположение:"
    }

    private fun initLocationTextField() = locationTextField.apply {
        prefHeight = 30.0
        prefWidth = 300.0
        layoutX = 136.0
        layoutY = 163.0
        styleClass.add("text-field-location")
        focusTraversableProperty().set(false)
        promptText = "Укажите расположение папки mods..."
    }

    private fun initLocationButton() = locationButton.apply {
        prefHeight = 30.0
        prefWidth = 36.0
        layoutX = 444.0
        layoutY = 163.0
        styleClass.add("button-location")
        mnemonicParsingProperty().set(false)
        focusTraversableProperty().set(false)
        text = "..."
        scaleble(this)
    }

    private fun initServersLabel() = serversLabel.apply {
        layoutX = 190.5
        layoutY = 215.0
        styleClass.add("label-add-servers")
        text = "Добавить сервера EP"
    }

    private fun initServersCheckBox() = serversCheckBox.apply {
        layoutX = 154.0
        layoutY = 211.0
        styleClass.add("check-box-add-servers")
        mnemonicParsingProperty().set(false)
        selectedProperty().set(true)
        contentDisplay = ContentDisplay.RIGHT
        textAlignment = TextAlignment.CENTER
    }

    private fun initInstallButton() = installButton.apply {
        prefHeight = 30.0
        prefWidth = 145.0
        layoutX = 177.5
        layoutY = 259.0
        styleClass.add("button-install")
        mnemonicParsingProperty().set(false)
        focusTraversableProperty().set(false)
        text = "Установить"
        scaleble(this)
    }

    private fun openMenuVersions() {
        contextMenu.show(
            this.versionButton.scene.window,
            this.versionButton.scene.window.x + this.textVersionButton.layoutX,
            this.versionButton.scene.window.y + 24 + this.textVersionButton.layoutY,
        )
    }

    private fun createContain(versions: MutableList<String>) {
        for (version in versions) {
            val versionItem = MenuItem(version)
            versionItem.styleClass.add("context-menu-versions-item")
            versionItem.setOnAction {
                textVersionButton.text = version
            }
            contextMenu.items.add(versionItem)
        }
    }

    private fun initializeButtonActions() {
        ButtonModsLocation(locationButton, locationTextField, mainPane)
        CheckBoxAddServers(serversCheckBox, mainPane)
        ButtonInstall(
            installButton,
            serversCheckBox,
            textVersionButton,
            locationTextField,
            locationButton,
            versionButton,
            modsButton,
            mainPane,
        )

        mainPane.setOnMouseClicked {
            mainPane.requestFocus()
        }
    }

    private fun scaleble(button: Button) {
        button.setOnMousePressed {
            button.scaleX = 0.87
            button.scaleY = 0.87
        }

        button.setOnMouseReleased {
            button.scaleX = 1.0
            button.scaleY = 1.0
        }
    }
}
