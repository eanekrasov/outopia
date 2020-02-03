val kotlinCoroutinesVersion: String by project
val kotlinSerializationVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlinx("coroutines-core-common", kotlinCoroutinesVersion))
                implementation(kotlinx("serialization-runtime-common", kotlinSerializationVersion))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
                implementation(kotlinx("serialization-runtime", kotlinSerializationVersion))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(kotlinx("coroutines-core-js", kotlinCoroutinesVersion))
                implementation(kotlinx("serialization-runtime-js", kotlinSerializationVersion))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}