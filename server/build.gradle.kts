import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springBootVersion: String by project
val kotlinCoroutinesVersion: String by project
val kotlinSerializationVersion: String by project
val kofuVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + arrayOf(
                "-Xjsr305=strict",
                "-Xjvm-default=enable",
                "-Xuse-experimental=kotlinx.serialization.UnstableDefault",
                "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer"
            )
        }
        dependsOn("processResources")
    }
    processResources {
        dependsOn(":js:browserWebpack")
        from(project(":js").projectDir.resolve("src/main/resources")) {
            into("static")
        }
        from(project(":js").buildDir.resolve("libs/Outopia-js.js"))  {
            into("static")
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // region Kotlin

    fun kotlinx(module: String, version: String): Any = "org.jetbrains.kotlinx:kotlinx-$module:$version"

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
    implementation(kotlinx("coroutines-reactor", kotlinCoroutinesVersion))
    implementation(kotlinx("serialization-runtime", kotlinSerializationVersion))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
    // endregion

    // region Spring Boot

    fun starter(module: String, version: String = springBootVersion): Any = "org.springframework.boot:spring-boot-starter-$module:$version"

    implementation(starter("data-jpa"))
    implementation(starter("thymeleaf"))
    implementation(starter("web"))
//    implementation(starter("security"))
    implementation(starter("websocket"))
    implementation(starter("test"))
    runtime("org.springframework.boot:spring-boot-devtools:$springBootVersion")

    // endregion

    // region bower

    fun bower(module: String, version: String = "+") = implementation("org.webjars.bower:$module:$version")

    implementation("org.webjars:webjars-locator-core")
    bower("bootstrap", "4.4.1")
    bower("jquery", "3.4.1")
    bower("popper.js", "1.16.0")
    bower("font-awesome", "4.7.0")
    bower("leaflet", "1.4.0")

    // endregion

    // region db

    runtime("mysql:mysql-connector-java")

    // endregion

//    #https://habr.com/ru/post/479954/
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("de.codecentric:spring-boot-admin-starter-server")
//    implementation("de.codecentric:spring-boot-admin-starter-client")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.all {
    exclude(module = "jakarta.validation-api")
    exclude(module = "hibernate-validator")
}