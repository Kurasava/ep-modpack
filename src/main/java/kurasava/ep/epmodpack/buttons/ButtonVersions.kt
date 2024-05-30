package kurasava.ep.epmodpack.buttons

import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Pane

class ButtonVersions(private val versionPreview: Button, private val button: Button, private val main: Pane) {
    private val contextMenu = ContextMenu()

    init {
        val versions = listOf("1.20.4", "1.20.3", "1.20.2", "1.20.1")
        versionPreview.text = versions.first()
        contextMenu.styleClass.add("context-menu-versions")
        this.createContain(versions)
        button.setOnMouseClicked {
            main.requestFocus()
            this.openMenuVersions()
        }
    }

    private fun openMenuVersions() {
        contextMenu.show(button.scene.window, button.scene.window.x + versionPreview.layoutX, button.scene.window.y + versionPreview.layoutY)
    }

    private fun createContain(versions: List<String>) {
        for (version in versions) {
            val versionItem = MenuItem(version)
            versionItem.styleClass.add("context-menu-versions-item")
            versionItem.styleProperty()
            versionItem.setOnAction {
                versionPreview.text = version
            }
            contextMenu.items.add(versionItem)
        }
    }
}
