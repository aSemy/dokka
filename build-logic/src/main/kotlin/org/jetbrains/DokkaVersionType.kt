package org.jetbrains

enum class DokkaVersionType(val suffix: Regex) {
    RELEASE("^$".toRegex()),
    RC("RC\\d?".toRegex()),
    SNAPSHOT("SNAPSHOT".toRegex()),
    DEV("dev-\\d+".toRegex());

    companion object {
        fun find(version: String?): DokkaVersionType? = when {
            version.isNullOrBlank() -> null
            else -> values().find {
                it.suffix.matches(version.substringAfter("-", ""))
            }
        }
    }
}
