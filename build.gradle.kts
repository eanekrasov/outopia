allprojects {
    group = "ru.o4fun"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.spring.io/milestone/")
        maven("https://repo.spring.io/snapshot/")
    }
}
