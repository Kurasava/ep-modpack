package kurasava.ep.epmodpack.buttons

import javafx.scene.control.CheckBox
import javafx.scene.layout.Pane
import kurasava.ep.epmodpack.App
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import org.json.JSONObject
import java.nio.file.Path

class CheckBoxAddServers(checkBox: CheckBox, private val main: Pane) {
    init {
        checkBox.setOnMouseClicked {
            main.requestFocus()
        }
    }

    companion object {
        fun addServers(dir: Path) {
            try {
                val file = dir.resolve("servers.dat").toFile()
                val rootTag = NBTUtil.read(file)
                val tag1 = rootTag.tag as CompoundTag
                val servers: ListTag<CompoundTag> = tag1.getListTag("servers") as ListTag<CompoundTag>
                val epServers = App.readUrls("json/servers.json")

                val existingIps = servers.map { it.getString("ip") }.toSet()

                epServers.asSequence()
                    .map { it as JSONObject }
                    .filterNot { existingIps.contains(it.getString("ip")) }
                    .map {
                        CompoundTag().apply {
                            putString("name", it.getString("name"))
                            putBoolean("hidden", it.getBoolean("hidden"))
                            putBoolean("acceptTextures", it.getBoolean("acceptTextures"))
                            putBoolean("preventsChatReports", it.getBoolean("preventsChatReports"))
                            putString("ip", it.getString("ip"))
                        }
                    }
                    .toList()
                    .forEachIndexed { i, e -> servers.add(i, e) }
                NBTUtil.write(rootTag, file, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
