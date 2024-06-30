package kurasava.ep.epmodpack.windows

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kurasava.ep.epmodpack.App
import kurasava.ep.epmodpack.Mod
import kurasava.ep.epmodpack.objects.Header

object ModsWindow {
    val mods = Pane()
    private val header = Header(false)
    private val scrollPane = ScrollPane()
    private val content = Pane()

    init {
        this.initModsPane()
        this.initScrollPane()
        this.initContent()
        this.scrollPane.content = this.content
        this.mods.children.addAll(this.header.header, this.scrollPane)
    }

    private fun initModsPane() =
        this.mods.apply {
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

    private fun initContent() {
        val optionalMods = App.mods
            .map { it as org.json.JSONObject }
            .filter { !it.getBoolean("required") }
            .filter { !it.getBoolean("hidden") }
            .map { Mod(it.getString("id")) }

        this.content.apply {
            prefHeight = 1730.0
            prefWidth = 485.0
            this@ModsWindow.generateModButtons(optionalMods).forEach { children.add(it) }
        }
    }

    fun getSelectedMods(): HashSet<Mod> {
        return content.children
            .filterIsInstance<CheckBox>()
            .filter { it.isSelected }
            .map { Mod(it.text) }.toHashSet()
    }

    private fun generateModButtons(optionalMods: List<Mod>): List<Node> {
        var checkBoxLayoutY = 10.0
        var textFlowLayoutY = 18.0
        var imageViewLayoutY = 20.0
        val set = ArrayList<Node>()
        for (mod in optionalMods.sortedBy { it.name.lowercase() }) {
            val checkBox = CheckBox().apply {
                prefHeight = 76.0
                prefWidth = 464.0
                layoutX = 10.0
                layoutY = checkBoxLayoutY
                styleClass.add("check-box-mod-normal")
                text = mod.id
            }

            val textFlow = TextFlow().apply {
                prefHeight = 74.0
                prefWidth = 369.0
                layoutX = 96.0
                layoutY = textFlowLayoutY
                stylesheets.add(this::class.java.getResource(App.STYLESHEET)!!.toExternalForm())
                mouseTransparentProperty().set(true)
            }

            val name = Text().apply {
                styleClass.add("mod-description-first-word")
                text = mod.name
            }

            val description = Text().apply {
                styleClass.add("mod-description")
                text = " - " + mod.description
            }

            val imageView = ImageView().apply {
                fitHeight = 55.0
                fitWidth = 55.0
                layoutX = 20.0
                layoutY = imageViewLayoutY
                styleClass.add("mod-image")
                image = Image(this::class.java.getResourceAsStream("/images/mod-icons/${mod.id}.png"))
            }

            textFlow.children.addAll(name, description)
            set.add(checkBox)
            set.add(textFlow)
            set.add(imageView)
            checkBoxLayoutY += 86
            textFlowLayoutY += 86
            imageViewLayoutY += 86
        }
        return set
    }
}
