package kurasava.ep.epmodpack.objects

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.windows.MainWindow.mainPane
import kotlin.system.exitProcess

class Header(private val isMain: Boolean) {
    val header = Pane()
    private val label = Label()
    private val hideApp = Button()
    private val closeApp = Button()
    private val backToMain = Button()
    private val icon = ImageView()

    private var xOffSet: Double = 0.0
    private var yOffSet: Double = 0.0

    init {
        this.initHeader()
        this.initLabel()
        this.initHideApp()
        this.initCloseApp()
        if (isMain) {
            this.initIcon()
            header.children.add(this.icon)
        } else {
            this.initBackToMain()
            header.children.addAll(this.backToMain)
        }

        header.setOnMouseClicked { mainPane.requestFocus() }
        header.children.addAll(label, hideApp, closeApp)
    }

    private fun initHeader() {
        this.header.apply {
            prefHeight = 40.0
            prefWidth = 500.0
            stylesheets.add(this::class.java.getResource(App.STYLESHEET)!!.toExternalForm())
            styleClass.add("header-pane")
            setOnMouseDragged {
                val stage = scene.window as Stage
                stage.x = it.screenX - xOffSet
                stage.y = it.screenY - yOffSet
            }
            setOnMousePressed {
                xOffSet = it.sceneX
                yOffSet = it.sceneY
            }
        }
    }

    private fun initLabel() {
        this.label.apply {
            prefHeight = 34.0
            prefWidth = if (isMain) 338.0 else 350.0
            layoutX = if (isMain) 46.0 else 118.5
            layoutY = 3.0
            stylesheets.add(this::class.java.getResource(App.STYLESHEET)!!.toExternalForm())
            styleClass.add("label-header")
            text = if (isMain) "Modpack installer 1.1" else "Выберите дополнительные моды"
        }
    }

    private fun initIcon() {
        this.icon.apply {
            fitHeight = 30.0
            fitWidth = 30.0
            layoutX = 6.0
            layoutY = 5.0
            pickOnBoundsProperty().set(true)
            preserveRatioProperty().set(true)
            image = Image("/images/icon-64px.png")
        }
    }

    private fun initHideApp() {
        this.decorateButton(this.hideApp, "button-hide")
        this.hideApp.setOnMouseClicked { hideOrClose(it.button, true) }
    }

    private fun initCloseApp() {
        this.decorateButton(this.closeApp, "button-exit")
        this.closeApp.setOnMouseClicked { hideOrClose(it.button, false) }
    }

    private fun initBackToMain() {
        this.backToMain.setOnMouseClicked { App.stage.scene = App.mainScene }
        this.decorateButton(this.backToMain, "button-back")
    }

    private fun decorateButton(button: Button, style: String) {
        button.apply {
            prefHeight = 39.0
            prefWidth = 48.0
            if (style == "button-hide") layoutX = 404.0 else if (style == "button-exit") layoutX = 452.0
            focusTraversableProperty().set(false)
            mnemonicParsingProperty().set(false)
            stylesheets.add(this::class.java.getResource(App.STYLESHEET)!!.toExternalForm())
            styleClass.add(style)
        }
    }

    private fun hideOrClose(button: MouseButton, hide: Boolean) {
        if (button != MouseButton.PRIMARY) return

        val stage = this.header.scene.window as Stage
        if (hide) stage.isIconified = true else {
            stage.close()
            Platform.exit()
            exitProcess(0)
        }

        stage.iconifiedProperty().addListener { _, _, isNowMinimized ->
            if (!isNowMinimized) {
                hideApp.style = ""
                hideApp.styleClass.add("button-hide")
            }
        }
    }
}
