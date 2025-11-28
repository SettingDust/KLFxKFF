import earth.terrarium.cloche.api.metadata.CommonMetadata
import groovy.lang.Closure

plugins {
    id("com.palantir.git-version") version "4.1.0"

    id("earth.terrarium.cloche") version "0.16.21-dust"
}

val archive_name: String by rootProject.properties
val id: String by rootProject.properties
val source: String by rootProject.properties

group = "settingdust.klf_x_kff"

val gitVersion: Closure<String> by extra
version = gitVersion()

base { archivesName = archive_name }

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        content {
            includeGroup("thedarkcolour")
        }
    }
    maven("https://repo.nyon.dev/releases") {
        content {
            includeGroup("dev.nyon")
        }
    }
    mavenCentral()
}

cloche {
    metadata {
        modId = id
        name = rootProject.property("name").toString()
        description = rootProject.property("description").toString()
        license = "Apache License 2.0"
        icon = "assets/$id/icon.png"
        sources = source
        issues = "$source/issues"
        author("SettingDust")

        dependency {
            modId = "minecraft"
            type = CommonMetadata.Dependency.Type.Required
            version {
                start = "1.20.1"
            }
        }
    }

    mappings {
        official()
    }
}