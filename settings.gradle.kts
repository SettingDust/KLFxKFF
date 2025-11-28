dependencyResolutionManagement {
    pluginManagement {
        repositories {
            mavenCentral()
            gradlePluginPortal()
            maven("https://maven.msrandom.net/repository/cloche")
            maven("https://raw.githubusercontent.com/SettingDust/cloche/refs/heads/maven-repo/")
            maven("https://raw.githubusercontent.com/SettingDust/minecraft-codev/refs/heads/maven-repo/")
            maven("https://raw.githubusercontent.com/SettingDust/jvm-multiplatform/refs/heads/maven-repo/")
            mavenLocal()
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "KLFxKFF"