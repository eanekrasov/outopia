val kotlinCoroutinesVersion: String by project
val kotlinSerializationVersion: String by project

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin.target.browser {}

dependencies {
    fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"

    implementation(project(":shared"))

    // region kotlin

    implementation(kotlin("stdlib-js"))
    implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
    implementation(kotlinx("serialization-runtime-js", kotlinSerializationVersion))

    // endregion

}