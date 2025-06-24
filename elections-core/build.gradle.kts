applyCommonConfiguration()

project.description = "Core"

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.7"
}

repositories {
    maven {
        name = "AuthLib"
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        name = "AnvilGUI"
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }
}

dependencies {
    api(project(":elections-api"))

    compileOnly(libs.spigot)
    compileOnly(libs.authlib)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.jspecify)

    api(libs.anvilgui)
    api(libs.fastboard)

    implementation(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.adventure.platform)
    implementation(libs.bstats)
    implementation(libs.xseries)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        minimize()
        archiveFileName.set("${rootProject.name}-${project.version}.jar")

        val shadePath = "de.eintosti.elections.util.external"
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        relocate("fr.mrmicky.fastboard", "$shadePath.fastboard")
        relocate("net.kyori", "$shadePath.kyori")
        relocate("net.wesjd.anvilgui", "$shadePath.anvilgui")
        relocate("org.bstats", "$shadePath.bstats")
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}