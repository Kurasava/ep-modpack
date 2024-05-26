package kurasava.ep.epmodpack

import org.json.JSONObject

class Mod(modId: String) {

    @JvmField
    var id: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var hidden: Boolean? = null

    @JvmField
    var required: Boolean? = null

    @JvmField
    var versions: HashMap<String, String> = HashMap()

    @JvmField
    var dependencies: HashSet<Mod> = HashSet()

    init {
        val jsonMod = getJsonMod(modId)
        this.id = jsonMod.getString("id")
        this.name = jsonMod.getString("name")
        this.hidden = jsonMod.getBoolean("hidden")
        this.required = jsonMod.getBoolean("required")
        val modLinks = jsonMod.getJSONObject("ver")
        for (key in modLinks.keys()) {
            this.versions[key] = modLinks.getString(key)
        }
        if (jsonMod.has("dependencies")) {
            val dependencies = jsonMod.getJSONArray("dependencies")
            dependencies.map { it as String }.forEach { this.dependencies.add(Mod(it)) }
        }
    }

    private fun getJsonMod(modId: String): JSONObject {
        return App.MODS.map { it as JSONObject }.first { it.getString("id") == modId }
    }
}