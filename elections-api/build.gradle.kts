applyCommonConfiguration()

project.description = "API"

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    compileOnly(libs.spigot)
    compileOnlyApi(libs.annotations)
    compileOnlyApi(libs.jspecify)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<Javadoc> {
        title = "Elections API (v" + project.version + ")"
        val opt = options as StandardJavadocDocletOptions
        opt.overview("javadoc/overview.html")
        opt.encoding("UTF-8")
        opt.charSet("UTF-8")
        opt.links("https://docs.oracle.com/javase/8/docs/api/")
        opt.links("https://hub.spigotmc.org/javadocs/spigot/")
        opt.links("https://javadoc.io/static/org.jetbrains/annotations/24.1.0/")
        opt.isLinkSource = true
        opt.isUse = true
        opt.keyWords()
    }
}

publishing {
    repositories {
        maven {
            name = "reposolite"
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.eintosti.de/snapshots")
            } else {
                uri("https://repo.eintosti.de/releases")
            }
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.eintosti"
            artifactId = "elections-api"
            version = project.version.toString()
            from(components["java"])
        }
    }
}