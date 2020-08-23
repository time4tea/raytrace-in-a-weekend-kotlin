package net.time4tea.raytrace.script

import java.io.File
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class Scripting() {
    val scriptEngineManager = ScriptEngineManager()
    val engine : ScriptEngine = scriptEngineManager.getEngineByExtension("kts")

    inline fun <reified T> load(file: File):T {
        return engine.eval(file.readText()) as T
    }
}