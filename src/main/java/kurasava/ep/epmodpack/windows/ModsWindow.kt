package kurasava.ep.epmodpack.windows

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.util.Duration
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.App.Companion.optionalMods
import kurasava.ep.epmodpack.Mod
import kurasava.ep.epmodpack.objects.Header
import java.util.concurrent.CompletableFuture

object ModsWindow {
    private lateinit var mods: Pane
    private lateinit var header: Header
    private lateinit var scrollPane: ScrollPane
    private var content: Pane? = null
    private val modIcons: HashSet<Pair<String, Image>> = this.getModIcons()


    fun initialize(version: String): Pane {
        this.mods = Pane()
        this.header = Header(false)
        this.scrollPane = ScrollPane()
        this.content = Pane()
        this.initModsPane()
        this.initScrollPane()
        this.initContent(version)
        this.scrollPane.content = this.content
        mods.children.addAll(this.header.header, this.scrollPane)
        return mods
    }

    private fun initModsPane() =
        mods.apply {
            prefHeight = 600.0
            prefWidth = 500.0
            styleClass.add("main-pane")
            focusTraversableProperty().set(true)
        }

    private fun initScrollPane() {
        this.scrollPane.apply {
            prefHeight = 560.0
            prefWidth = 500.0
            layoutY = 40.0
            styleClass.add("scroll-pane-mods")
        }
    }

    private fun initContent(version: String) {
        this.content.apply {
            this?.prefWidth = 485.0
            this?.children?.addAll(this@ModsWindow.generateModButtons(App.optionalMods, version))
        }
    }

    fun getSelectedMods(): HashSet<Mod> {
        if (content == null) return HashSet<Mod>()
        return content!!.children
            .filterIsInstance<CheckBox>()
            .filter { it.isSelected }
            .map { Mod(it.text) }.toHashSet()
    }

    private fun generateModButtons(optionalMods: List<Mod>, version: String): List<Node> {
        var checkBoxLayoutY = 10.0
        var textFlowLayoutY = 18.0
        var imageViewLayoutY = 20.0
        val set = ArrayList<Node>()
        for (mod in optionalMods.sortedBy { it.id }) {
           lateinit var alertImage: ImageView

            val checkBox = CheckBox().apply {
                prefHeight = 76.0
                prefWidth = 464.0
                layoutX = 10.0
                layoutY = checkBoxLayoutY
                if (mod.isReleased(version)) {
                    styleClass.add("check-box-mod-normal")
                } else {
                    styleClass.add("check-box-mod-not-released")
                    isMouseTransparent = true
                    alertImage = ImageView().apply {
                        fitHeight = 24.0
                        fitWidth = 24.0
                        layoutX = 7.0
                        layoutY = imageViewLayoutY - 13
                        styleClass.add("mod-not-released-alert-image")
                        image = Image("images/alert.png")
                    }
                    Tooltip.install(alertImage, Tooltip("Мод недоступен на этой версии").apply {
                        showDelay = Duration(200.0)
                        styleClass.add("tooltip-alert")
                    })
                }
                text = mod.id
            }

            val textFlow = TextFlow().apply {
                prefHeight = 74.0
                prefWidth = 369.0
                layoutX = 96.0
                layoutY = textFlowLayoutY
                mouseTransparentProperty().set(true)
            }

            val name = Text().apply {
                styleClass.add("mod-description-first-word")
                text = mod.name
            }

            val description = Text().apply {
                styleClass.add("mod-description")
                text = " - " + mod.description
                mouseTransparentProperty().set(true)
            }

            val iconImage = ImageView().apply {
                fitHeight = 55.0
                fitWidth = 55.0
                layoutX = 20.0
                layoutY = imageViewLayoutY
                styleClass.add("mod-image")
                CompletableFuture.runAsync {
                    image = modIcons.find { it.first == mod.id }!!.second
                }
                mouseTransparentProperty().set(true)
            }

            textFlow.children.addAll(name, description)
            set.add(checkBox)
            set.add(textFlow)
            set.add(iconImage)
            if (!mod.isReleased(version)) set.add(alertImage)
            checkBoxLayoutY += 86
            textFlowLayoutY += 86
            imageViewLayoutY += 86
        }
        this.content?.prefHeight = checkBoxLayoutY
        return set
    }

    private fun getModIcons(): HashSet<Pair<String, Image>> {
        val modIcons = HashSet<Pair<String, Image>>()
        optionalMods.forEach {
            modIcons.add(Pair(it.id, Image(this.javaClass.getResourceAsStream("/icons/${it.id}.png"))))
        }
        return modIcons
    }
}
