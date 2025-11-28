@file:Suppress("UnstableApiUsage")

import earth.terrarium.cloche.api.metadata.CommonMetadata
import earth.terrarium.cloche.api.target.FabricTarget
import earth.terrarium.cloche.api.target.ForgeLikeTarget
import earth.terrarium.cloche.api.target.MinecraftTarget
import groovy.lang.Closure
import net.msrandom.minecraftcodev.core.utils.lowerCamelCaseGradleName

plugins {
    id("com.palantir.git-version") version "4.2.0"

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
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

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

    maven("https://maven.lenni0451.net/snapshots/") {
        content {
            includeGroupAndSubgroups("net.lenni0451")
        }
    }

    mavenCentral()

    cloche {
        librariesMinecraft()
        main()
        mavenFabric()
        mavenForge()
        mavenNeoforged()
        mavenNeoforgedMeta()
        mavenParchment()
    }
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
            version("1.20.1")
        }
    }

    mappings {
        official()
    }

    common {}

    val commonCommon = common("common:common")

    val forge = forge {
        dependsOn(commonCommon)

        minecraftVersion = "1.20.1"
        loaderVersion = "47.4.4"

        dependencies {
            implementation("maven.modrinth:preloading-tricks:2.7.1")
            implementation("net.lenni0451:Reflect:1.6.0-SNAPSHOT")

            modImplementation("dev.nyon:KotlinLangForge:2.10.6-k2.2.21-2.0+forge")
            modImplementation("thedarkcolour:kotlinforforge:4.11.0")
        }

        tasks {
            named(generateModsTomlTaskName) {
                enabled = false
            }
        }
    }

    forge("forge:run") {
        minecraftVersion = "1.20.1"
        loaderVersion = "47.4.4"

        runs {
            client {
                env("MOD_CLASSES", "")
            }
        }

        dependencies {
            implementation(project(":")) {
                capabilities {
                    requireFeature(forge.capabilitySuffix!!)
                }
            }
        }

        tasks {
            named(jarTaskName) {
                enabled = false
            }

            named(remapJarTaskName) {
                enabled = false
            }

            named(includeJarTaskName) {
                enabled = false
            }

            named(accessWidenTaskName) {
                dependsOn(forge.accessWidenTaskName)
            }
        }
    }
}

val SourceSet.includeJarTaskName: String
    get() = lowerCamelCaseGradleName(takeUnless(SourceSet::isMain)?.name, "includeJar")

val MinecraftTarget.includeJarTaskName: String
    get() = when (this) {
        is FabricTarget -> sourceSet.includeJarTaskName
        is ForgeLikeTarget -> sourceSet.includeJarTaskName
        else -> throw IllegalArgumentException("Unsupported target $this")
    }

val ForgeLikeTarget.generateModsTomlTaskName: String
    get() = lowerCamelCaseGradleName("generate", featureName, "modsToml")

val MinecraftTarget.jarTaskName: String
    get() = lowerCamelCaseGradleName(featureName, "jar")

val MinecraftTarget.remapJarTaskName: String
    get() = lowerCamelCaseGradleName(featureName, "remapJar")

val MinecraftTarget.accessWidenTaskName: String
    get() = lowerCamelCaseGradleName("accessWiden", featureName, "minecraft")