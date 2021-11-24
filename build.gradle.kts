import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.tribot.gradle.plugin.getTribotDirectory

val baseDir = projectDir
val outputDir = getTribotDirectory().resolve("bin")

plugins {
    java
    kotlin("jvm") version "1.4.10"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

allprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.openjfx.javafxplugin")

    java.sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
    java.targetCompatibility = org.gradle.api.JavaVersion.VERSION_11

    repositories {
        jcenter()
        maven("https://gitlab.com/api/v4/projects/20741387/packages/maven") // Tribot Central
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        api(files("${baseDir.absolutePath}/allatori-annotations-7.5.jar"))
        api("org.tribot:tribot-script-sdk:0.0.42")
    }

    sourceSets {
        main {
            java {
                setSrcDirs(listOf("src"))
            }
            resources {
                setSrcDirs(listOf("src/resources"))
            }
            output.resourcesDir = outputDir
            java.outputDir = outputDir
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        destinationDir = outputDir
    }

    configurations.all {
        // Ensures that our dependencies will update timely
        resolutionStrategy.cacheDynamicVersionsFor(5, "minutes")
        resolutionStrategy.cacheChangingModulesFor(5, "minutes")
    }

    tasks["clean"].doFirst {
        outputDir.listFiles()?.forEach { it.deleteRecursively() }
    }
}

val repoCopy = tasks.create("repoCopy") {
    group = "tribot"
    doLast {
        val repoDeployDir = this.project.projectDir.resolve("build/repo-deploy").also { it.mkdirs() }
        allprojects
                .filter { it.path != project.path }
                .map { it.projectDir.resolve("build/repo-deploy") }
                .filter { it.exists() }
                .mapNotNull { it.listFiles()?.getOrNull(0) }
                .forEach { it.copyTo(repoDeployDir.resolve(it.name), overwrite = true) }
    }
}

tasks.build {
    finalizedBy(repoCopy)
}