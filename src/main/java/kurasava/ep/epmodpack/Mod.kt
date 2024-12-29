package kurasava.ep.epmodpack

import org.json.JSONObject

class Mod(modId: String) {
    var id: String
    var name: String
    var description: String?
    var hidden: Boolean
    var required: Boolean
    var versions: HashMap<String, String> = HashMap()
    var dependencies: HashSet<Mod> = HashSet()

    init {
        val jsonMod = getJsonMod(modId)
        this.id = jsonMod.getString("id")
        this.name = jsonMod.getString("name")
        this.description = if (jsonMod.has("description")) jsonMod.getString("description") else null
        this.hidden = jsonMod.getBoolean("hidden")
        this.required = jsonMod.getBoolean("required")
        val modLinks = jsonMod.getJSONObject("versions")
        for (key in modLinks.keys()) {
            this.versions[key] = modLinks.getString(key)
        }
        if (jsonMod.has("dependencies")) {
            val dependencies = jsonMod.getJSONArray("dependencies")
            dependencies.map { it as String }.forEach { this.dependencies.add(Mod(it)) }
        }
    }

    private fun getJsonMod(modId: String): JSONObject {
       return App.mods.map { it as JSONObject }.first { it.getString("id") == modId }
    }


    fun isReleased(version: String) : Boolean = this.versions[version]?.let { it != "null" && it.isNotEmpty() } ?: false
}
