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
                "-Xuse-experimental=kotlinx.serialization.UnstableDefault"
            )
        }
        dependsOn("processResources")
    }
    processResources {
        dependsOn(":js:browserWebpack")
        from(project(":js").projectDir.resolve("src/main/resources")) {
            into("static")
        }
        from(project(":js").buildDir.resolve("libs/Outopia-js.js")) {
            into("static")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot.experimental:spring-boot-bom-r2dbc:0.1.0.BUILD-SNAPSHOT")
    }
}

dependencies {
    fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
    fun boot(module: String, version: String = springBootVersion) = "org.springframework.boot:spring-boot-$module:$version"
    fun starter(module: String, version: String = springBootVersion) = "org.springframework.boot:spring-boot-starter-$module:$version"
    fun bower(module: String, version: String = "+") = "org.webjars.bower:$module:$version"

    implementation(project(":shared"))
//    #https://habr.com/ru/post/479954/
    implementation(starter("actuator"))
//    implementation("de.codecentric:spring-boot-admin-starter-server")
//    implementation("de.codecentric:spring-boot-admin-starter-client")

    // region Kotlin

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
    implementation(kotlinx("coroutines-reactor", kotlinCoroutinesVersion))
    implementation(kotlinx("serialization-runtime", kotlinSerializationVersion))

    // endregion

    // region Spring Boot

    implementation("org.springframework.boot.experimental:spring-boot-starter-data-r2dbc")
    implementation(starter("thymeleaf"))
    implementation(starter("webflux"))
    runtimeOnly(boot("devtools"))

    // endregion

    // region bower

    implementation("org.webjars:webjars-locator-core")
    implementation(bower("bootstrap", "4.4.1"))
    implementation(bower("jquery", "3.4.1"))
    implementation(bower("popper.js", "1.16.0"))
    implementation(bower("font-awesome", "4.7.0"))
    implementation(bower("leaflet", "1.4.0"))

    // endregion

    // region db

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.flywaydb:flyway-core:6.2.1")

    // endregion

    // region tests

    testImplementation(starter("test")) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot.experimental:spring-boot-test-autoconfigure-r2dbc")
    testImplementation("io.projectreactor:reactor-test")

    // endregion

}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.all {
    exclude(module = "jakarta.validation-api")
    exclude(module = "hibernate-validator")
}