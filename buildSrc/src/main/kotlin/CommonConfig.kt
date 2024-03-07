import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

fun Project.applyCommonConfiguration() {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven {
            name = "Spigot"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
    }

    plugins.withId("java") {
        the<JavaPluginExtension>().sourceCompatibility = JavaVersion.VERSION_1_8
        the<JavaPluginExtension>().targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
}